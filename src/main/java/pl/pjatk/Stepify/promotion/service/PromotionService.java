package pl.pjatk.Stepify.promotion.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import pl.pjatk.Stepify.cart.model.Cart;
import pl.pjatk.Stepify.cart.repository.CartRepository;
import pl.pjatk.Stepify.exception.ResourceNotFoundException;
import pl.pjatk.Stepify.order.model.Order;
import pl.pjatk.Stepify.promotion.dto.ApplyPromotionDTO;
import pl.pjatk.Stepify.promotion.dto.PromotionDTO;
import pl.pjatk.Stepify.promotion.exception.InvalidPromotionException;
import pl.pjatk.Stepify.promotion.mapper.PromotionMapper;
import pl.pjatk.Stepify.promotion.model.AppliedPromotion;
import pl.pjatk.Stepify.promotion.model.Promotion;
import pl.pjatk.Stepify.promotion.repository.AppliedPromotionRepository;
import pl.pjatk.Stepify.promotion.repository.PromotionRepository;
import pl.pjatk.Stepify.user.model.User;
import pl.pjatk.Stepify.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static pl.pjatk.Stepify.promotion.model.PromotionType.FIXED_AMOUNT_DISCOUNT;
import static pl.pjatk.Stepify.promotion.model.PromotionType.PERCENTAGE_DISCOUNT;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final AppliedPromotionRepository appliedPromotionRepository;
    private final PromotionMapper promotionMapper;
    private final UserService userService;
    private final CartRepository cartRepository;

    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        promotionDTO.setActive(isActive(promotionDTO.getStartDate(), promotionDTO.getEndDate()));
        promotionRepository.save(promotionMapper.mapPromotionDTOToPromotion(promotionDTO));
        return promotionDTO;
    }

    public List<PromotionDTO> getActivePromotions() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findAllByActiveIsTrueAndStartDateBeforeAndEndDateAfter(now, now)
                .stream()
                .map(promotionMapper::mapPromotionToPromotionDTO)
                .collect(Collectors.toList());
    }

    public List<PromotionDTO> getAllPromotions() {
        return promotionRepository.findAll()
                .stream()
                .map(promotionMapper::mapPromotionToPromotionDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Cart applyPromotion(ApplyPromotionDTO applyPromotionDTO) {
        User currentUser = userService.getCurrentUser();
        Cart cart = cartRepository.findCartByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Promotion promotion = promotionRepository.findByCodeAndActiveIsTrue(applyPromotionDTO.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid promotion code"));

        validatePromotion(promotion, cart);
        double discountAmount = calculateDiscountAmount(promotion, cart);

        AppliedPromotion appliedPromotion = new AppliedPromotion();
        appliedPromotion.setPromotion(promotion);
        appliedPromotion.setCart(cart);
        appliedPromotion.setDiscountAmount(discountAmount);
        appliedPromotion.setAppliedAt(LocalDateTime.now());

        cart.getAppliedPromotions().add(appliedPromotion);

        promotion.setUsageCount(promotion.getUsageCount() + 1);
        promotionRepository.save(promotion);

        cart.recalculateTotalPrice();

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removePromotion(Long promotionId) {
        User currentUser = userService.getCurrentUser();
        Cart cart = cartRepository.findCartByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        // Find and remove the applied promotion
        cart.getAppliedPromotions().removeIf(ap -> ap.getPromotion().getId() == (promotionId));

        // Recalculate cart totals
        cart.recalculateTotalPrice();

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart recalculateCartPromotions(Cart cart) {
        if (cart.getAppliedPromotions() == null || cart.getAppliedPromotions().isEmpty()) {
            return cart; // No promotions to recalculate
        }

        // Reset all discount amounts first
        cart.getAppliedPromotions().forEach(ap -> ap.setDiscountAmount(0.0));

        // Recalculate each promotion with current cart state
        for (AppliedPromotion appliedPromotion : cart.getAppliedPromotions()) {
            Promotion promotion = appliedPromotion.getPromotion();

            // Skip invalid promotions
            if (!isPromotionValid(promotion)) {
                continue;
            }

            // Calculate new discount amount based on current cart state
            double discountAmount = calculateDiscountAmount(promotion, cart);
            appliedPromotion.setDiscountAmount(discountAmount);
        }

        // Recalculate cart totals
        cart.recalculateTotalPrice();
        return cart;
    }

    public void transferPromotionsToOrder(Cart cart, Order order) {
        for (AppliedPromotion cartPromotion : cart.getAppliedPromotions()) {
            AppliedPromotion orderPromotion = new AppliedPromotion();
            orderPromotion.setPromotion(cartPromotion.getPromotion());
            orderPromotion.setOrder(order);
            orderPromotion.setDiscountAmount(cartPromotion.getDiscountAmount());
            orderPromotion.setAppliedAt(LocalDateTime.now());

            order.getAppliedPromotions().add(orderPromotion);
        }

        order.recalculateTotalPrice();
    }


    private void validatePromotion(Promotion promotion, Cart cart) {
        LocalDateTime now = LocalDateTime.now();

        // Check if promotion is valid
        if (!promotion.isActive()) {
            throw new InvalidPromotionException("Promotion is not active");
        }

        if (now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
            throw new InvalidPromotionException("Promotion is not valid at this time");
        }

        // Check usage limit
        if (promotion.getUsageLimit() != null && promotion.getUsageCount() >= promotion.getUsageLimit()) {
            throw new InvalidPromotionException("Promotion usage limit exceeded");
        }

        // Check minimum order value
        if (promotion.getMinimumOrderValue() != null && cart.getTotalPrice() < promotion.getMinimumOrderValue()) {
            throw new InvalidPromotionException("Order total does not meet minimum value requirement");
        }

        // Check for duplicate promotions
        if (!promotion.isStackable() && !cart.getAppliedPromotions().isEmpty()) {
            throw new InvalidPromotionException("This promotion cannot be combined with other promotions");
        }

        // Check for already applied
        boolean alreadyApplied = cart.getAppliedPromotions().stream()
                .anyMatch(ap -> ap.getPromotion().getId() == (promotion.getId()));
        if (alreadyApplied) {
            throw new InvalidPromotionException("Promotion already applied");
        }
    }

    public double calculateDiscountAmount(Promotion promotion, Cart cart) {

        return switch (promotion.getPromotionType()) {
            case PERCENTAGE_DISCOUNT -> cart.getTotalPrice() * (promotion.getValue() / 100.0);
            case FIXED_AMOUNT_DISCOUNT -> Math.min(promotion.getValue(), cart.getTotalPrice());
        };
    }

    private boolean isActive(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    private boolean isPromotionValid(Promotion promotion) {
        LocalDateTime now = LocalDateTime.now();
        return promotion.isActive() &&
                now.isAfter(promotion.getStartDate()) &&
                now.isBefore(promotion.getEndDate()) &&
                (promotion.getUsageLimit() == null || promotion.getUsageCount() < promotion.getUsageLimit());
    }

}
