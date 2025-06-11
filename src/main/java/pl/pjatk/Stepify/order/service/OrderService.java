package pl.pjatk.Stepify.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pjatk.Stepify.cart.model.Cart;
import pl.pjatk.Stepify.cart.repository.CartRepository;
import pl.pjatk.Stepify.cart.service.CartService;
import pl.pjatk.Stepify.exception.ResourceNotFoundException;
import pl.pjatk.Stepify.exception.UnauthorizedAccessException;
import pl.pjatk.Stepify.order.dto.OrderDTO;
import pl.pjatk.Stepify.order.dto.OrderSummaryDTO;
import pl.pjatk.Stepify.order.mapper.OrderMapper;
import pl.pjatk.Stepify.order.model.*;
import pl.pjatk.Stepify.order.repository.OrderRepository;
import pl.pjatk.Stepify.payment.model.Payment;
import pl.pjatk.Stepify.payment.model.PaymentStatus;
import pl.pjatk.Stepify.promotion.service.PromotionService;
import pl.pjatk.Stepify.user.mapper.AddressMapper;
import pl.pjatk.Stepify.user.model.User;
import pl.pjatk.Stepify.user.repository.AddressRepository;
import pl.pjatk.Stepify.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final CartService cartService;
    private final PromotionService promotionService;

    public OrderDTO order() {
        User user = userService.getCurrentUser();

        Cart cart = cartRepository.findCartByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        List<OrderItem> orderItems = cart
                .getCartItems()
                .stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setBrandName(cartItem.getProduct().getBrandName());
                    orderItem.setModelName(cartItem.getProduct().getModelName());
                    orderItem.setColor(cartItem.getColor());
                    orderItem.setSize(cartItem.getSize());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getPrice());
                    return orderItem;
                }).toList();

        Order order = new Order(
                orderItems,
                cart.getTotalPrice()
        );
        orderItems.forEach(orderItem -> {orderItem.setOrder(order);});


        return orderMapper.mapOrderToOrderDTO(order);
    }

    public OrderSummaryDTO placeOrder(OrderDTO orderDTO) {

        User user = userService.getCurrentUser();

        Order order = orderMapper.mapOrderDTOToOrder(orderDTO);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.AWAITING_PAYMENT);
        order.setPayment(new Payment(orderDTO.getPayment().getPaymentMethod(), orderDTO.getTotalPrice(), PaymentStatus.PENDING, order));

        Cart cart = cartRepository.findCartByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        promotionService.transferPromotionsToOrder(cart, order);


        if (order.getDeliveryMethod().equals(DeliveryMethod.COURIER)) {
            order.setTotalPrice(order.getTotalPrice() + 10.0);
        } else if (order.getDeliveryMethod().equals(DeliveryMethod.PARCEL)) {
            order.setTotalPrice(order.getTotalPrice() + 5.0);
        }

        orderRepository.save(order);
        cartService.clearCart();

        return orderMapper.mapOrderToOrderSummaryDTO(orderRepository.save(order));
    }

    public List<OrderDTO> getOrders() {
        User user = userService.getCurrentUser();
        List<Order> orders = orderRepository.findAllByUserId(user.getId());

        return orders
                .stream()
                .map(orderMapper::mapOrderToOrderDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository
                .findAll()
                .stream()
                .map(orderMapper::mapOrderToOrderDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(long id) {
        User user = userService.getCurrentUser();
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getUser().getId() != user.getId()) {
            throw new UnauthorizedAccessException("You do not have permission to access this order");
        } else {
            return orderMapper.mapOrderToOrderDTO(order);
        }
    }
}


