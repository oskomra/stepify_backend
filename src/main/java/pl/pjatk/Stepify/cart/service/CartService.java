package pl.pjatk.Stepify.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.Stepify.cart.dto.CartDTO;
import pl.pjatk.Stepify.cart.dto.CartItemDTO;
import pl.pjatk.Stepify.cart.dto.RequestColorAndSizeDTO;
import pl.pjatk.Stepify.cart.mapper.CartItemMapper;
import pl.pjatk.Stepify.cart.mapper.CartMapper;
import pl.pjatk.Stepify.cart.model.Cart;
import pl.pjatk.Stepify.cart.model.CartItem;
import pl.pjatk.Stepify.cart.repository.CartItemRepository;
import pl.pjatk.Stepify.cart.repository.CartRepository;
import pl.pjatk.Stepify.exception.ResourceNotFoundException;
import pl.pjatk.Stepify.exception.UnauthorizedAccessException;
import pl.pjatk.Stepify.product.model.Product;
import pl.pjatk.Stepify.product.repository.ProductRepository;
import pl.pjatk.Stepify.user.model.User;
import pl.pjatk.Stepify.user.service.UserService;


@Service
@RequiredArgsConstructor
public class CartService {


    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;


    public CartDTO getCart() {
        User currentUser = userService.getCurrentUser();
        return cartMapper.mapCartToCartDTO(
                cartRepository.findCartByUserId(currentUser.getId())
                        .orElseGet(() -> cartRepository.save(new Cart(currentUser)))
        );
    }

    public CartItemDTO addItem(String brandName, String modelName, RequestColorAndSizeDTO colorAndSize) {
        Product product = productRepository.findProductByBrandNameAndModelName(brandName, modelName)
                .orElseThrow(() -> new ResourceNotFoundException("Product " + brandName + " " + modelName + " not found"));

        Cart cart = cartRepository.findCartByUserId(userService.getCurrentUser().getId())
                .orElseGet(() -> cartRepository.save(new Cart(userService.getCurrentUser())));

        CartItem newCartItem = new CartItem(product, colorAndSize.getColor(), colorAndSize.getSize(), 1, cart);

        if(!isColorValid(product, colorAndSize.getColor()) || !isSizeValid(product, colorAndSize.getColor(), colorAndSize.getSize())) {
            throw new ResourceNotFoundException("Product " + brandName + " " + modelName + " not found");
        }

        if (isItemInCart(cart, product, colorAndSize.getColor(), colorAndSize.getSize())) {
            increaseQuantityForItem(cart, product, colorAndSize.getColor(), colorAndSize.getSize());
        } else {
            cart.getCartItems().add(newCartItem);
        }

        cart.recalculateTotalPrice();
        cartRepository.save(cart);

        return cartItemMapper.mapCartItemToCartItemDTO(newCartItem);
    }

    @Transactional
    public CartDTO deleteItem(long id) {
        CartItem item = getCartItemForCurrentUser(id);
        Cart cart = item.getCart();

        cart.getCartItems().remove(item);
        cart.recalculateTotalPrice();

        return cartMapper.mapCartToCartDTO(cartRepository.save(cart));
    }

    @Transactional
    public CartDTO updateItemQuantity(long id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        CartItem item = getCartItemForCurrentUser(id);
        Cart cart = item.getCart();

        item.setQuantity(quantity);
        cart.recalculateTotalPrice();

        return cartMapper.mapCartToCartDTO(cartRepository.save(cart));
    }

    private boolean isItemInCart(Cart cart, Product product, String color, double size) {
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return false;
        } else {
            return cart.getCartItems().stream()
                    .anyMatch(item ->
                            item.getProduct().getId() == product.getId() &&
                                    item.getColor().equalsIgnoreCase(color) &&
                                    item.getSize() == size);
        }
    }

    private void increaseQuantityForItem(Cart cart, Product product, String color, double size) {
        for (CartItem item : cart.getCartItems()) {
            if (item.getProduct().getId() == product.getId() && item.getColor().equals(color) && item.getSize() == size) {
                item.setQuantity(item.getQuantity() + 1);
            }
        }
    }

    private CartItem getCartItemForCurrentUser(long itemId) {
        User currentUser = userService.getCurrentUser();

        Cart cart = cartRepository.findCartByUserId(currentUser.getId())
                .orElseThrow();

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (item.getCart().getId() != cart.getId()) {
            throw new UnauthorizedAccessException("You do not have permission to access this cart item.");
        }

        return item;
    }

    private boolean isColorValid(Product product, String color) {
        return product.getColors()
                .stream()
                .anyMatch(c -> c.getColor().equalsIgnoreCase(color));
    }

    private boolean isSizeValid(Product product, String color, double size) {
        return product.getColors()
                .stream()
                .filter(c -> c.getColor().equalsIgnoreCase(color))
                .anyMatch(c -> c.getSizes().stream()
                        .anyMatch(s -> s.getSize() == size));
    }


}
