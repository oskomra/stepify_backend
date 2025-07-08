package pl.pjatk.Stepify.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pjatk.Stepify.cart.model.Cart;
import pl.pjatk.Stepify.cart.repository.CartRepository;
import pl.pjatk.Stepify.cart.service.CartService;
import pl.pjatk.Stepify.exception.ResourceNotFoundException;
import pl.pjatk.Stepify.exception.UnauthorizedAccessException;
import pl.pjatk.Stepify.order.dto.OrderCancellationDTO;
import pl.pjatk.Stepify.order.dto.OrderDTO;
import pl.pjatk.Stepify.order.dto.OrderStatusUpdateDTO;
import pl.pjatk.Stepify.order.dto.OrderSummaryDTO;
import pl.pjatk.Stepify.order.mapper.OrderMapper;
import pl.pjatk.Stepify.order.model.*;
import pl.pjatk.Stepify.order.repository.OrderRepository;
import pl.pjatk.Stepify.payment.model.Payment;
import pl.pjatk.Stepify.payment.model.PaymentStatus;
import pl.pjatk.Stepify.promotion.service.PromotionService;
import pl.pjatk.Stepify.user.model.User;
import pl.pjatk.Stepify.user.service.UserService;
import pl.pjatk.Stepify.product.repository.ProductRepository;
import pl.pjatk.Stepify.product.repository.ProductSizeRepository;

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
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;

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


            return orderMapper.mapOrderToOrderDTO(order);

    }

    public OrderDTO updateOrderStatus(long id, OrderStatusUpdateDTO statusUpdateDTO) {
        User user = userService.getCurrentUser();
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Only admin can update order status
        if (!user.getAuthority().equals("ROLE_ADMIN")) {
            throw new UnauthorizedAccessException("Only administrators can update order status");
        }

        // Validate status transition
        validateStatusTransition(order.getStatus(), statusUpdateDTO.getStatus());

        order.setStatus(statusUpdateDTO.getStatus());
        return orderMapper.mapOrderToOrderDTO(orderRepository.save(order));
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case AWAITING_PAYMENT:
                if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalArgumentException("Invalid status transition from AWAITING_PAYMENT");
                }
                break;
            case CONFIRMED:
                if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalArgumentException("Invalid status transition from CONFIRMED");
                }
                break;
            case SHIPPED:
                if (newStatus != OrderStatus.DELIVERED && newStatus != OrderStatus.FAILED) {
                    throw new IllegalArgumentException("Invalid status transition from SHIPPED");
                }
                break;
            case DELIVERED:
                throw new IllegalArgumentException("Cannot change status of a DELIVERED order");
            case CANCELLED:
                throw new IllegalArgumentException("Cannot change status of a CANCELLED order");
            case FAILED:
                throw new IllegalArgumentException("Cannot change status of a FAILED order");
            default:
                throw new IllegalArgumentException("Invalid current status");
        }
    }

    public OrderDTO cancelOrder(long id) {
        User user = userService.getCurrentUser();
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Verify that the user owns the order
        if (order.getUser().getId() != user.getId()) {
            throw new UnauthorizedAccessException("You can only cancel your own orders");
        }

        // Check if the order can be cancelled
        if (!canBeCancelled(order.getStatus())) {
            throw new IllegalArgumentException("Order cannot be cancelled in its current state: " + order.getStatus());
        }

        // Update order status to cancelled
        order.setStatus(OrderStatus.CANCELLED);
        
        // If the order was in AWAITING_PAYMENT state, we might want to handle the payment cancellation
        if (order.getStatus() == OrderStatus.AWAITING_PAYMENT && order.getPayment() != null) {
            order.getPayment().setPaymentStatus(PaymentStatus.CANCELLED);
        }

        // If the order was already confirmed, we should handle inventory
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            // Return items to inventory
            returnItemsToInventory(order.getOrderItems());
        }

        return orderMapper.mapOrderToOrderDTO(orderRepository.save(order));
    }

    private boolean canBeCancelled(OrderStatus status) {
        return status == OrderStatus.AWAITING_PAYMENT || status == OrderStatus.CONFIRMED;
    }

    private void returnItemsToInventory(List<OrderItem> orderItems) {
        orderItems.forEach(orderItem -> {
            productRepository.findProductByBrandNameAndModelName(orderItem.getBrandName(), orderItem.getModelName())
                    .flatMap(product -> product.getColors().stream()
                            .filter(color -> color.getColor().equalsIgnoreCase(orderItem.getColor()))
                            .findFirst()
                            .flatMap(color -> color.getSizes().stream()
                                    .filter(size -> size.getSize() == orderItem.getSize())
                                    .findFirst()))
                    .ifPresent(size -> {
                        size.setStock(size.getStock() + orderItem.getQuantity());
                        productSizeRepository.save(size);
                    });
        });
    }
}


