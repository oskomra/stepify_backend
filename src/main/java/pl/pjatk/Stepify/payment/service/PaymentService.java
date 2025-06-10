package pl.pjatk.Stepify.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.pjatk.Stepify.cart.exception.OutOfStockException;
import pl.pjatk.Stepify.cart.service.CartService;
import pl.pjatk.Stepify.exception.ResourceNotFoundException;
import pl.pjatk.Stepify.exception.UnauthorizedAccessException;
import pl.pjatk.Stepify.order.model.Order;
import pl.pjatk.Stepify.order.model.OrderItem;
import pl.pjatk.Stepify.order.model.OrderStatus;
import pl.pjatk.Stepify.order.repository.OrderRepository;
import pl.pjatk.Stepify.payment.dto.PaymentDTO;
import pl.pjatk.Stepify.payment.dto.RequestPaymentDetailsDTO;
import pl.pjatk.Stepify.payment.mapper.PaymentMapper;
import pl.pjatk.Stepify.payment.model.Payment;
import pl.pjatk.Stepify.payment.model.PaymentStatus;
import pl.pjatk.Stepify.payment.repository.PaymentRepository;
import pl.pjatk.Stepify.product.repository.ProductRepository;
import pl.pjatk.Stepify.product.service.ProductService;
import pl.pjatk.Stepify.user.model.User;
import pl.pjatk.Stepify.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;


    public PaymentDTO getPaymentByOrderId(Long orderId) {
        User user = userService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getUser().getId() != user.getId()) {
            throw new UnauthorizedAccessException("Unauthorized access");
        } else {
            Payment payment = paymentRepository.findPaymentByOrderId(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
            return paymentMapper.paymentToPaymentDTO(payment);
        }
    }

    public PaymentDTO processPayment(long id, RequestPaymentDetailsDTO requestPaymentDetailsDTO) {
        User user = userService.getCurrentUser();
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));


        List<OrderItem> orderItemsList = payment.getOrder().getOrderItems();
        if (!productService.isProductStockAvailableOnOrder(orderItemsList)) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            Order order = payment.getOrder();
            order.setStatus(OrderStatus.FAILED);

            orderRepository.save(order);
            paymentRepository.save(payment);
            cartService.clearCart();
            throw new OutOfStockException("One or more products are out of stock");
        }

        productService.updateProductStockOnOrder(orderItemsList);

        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(UUID.randomUUID().toString());

        Order order = payment.getOrder();
        order.setStatus(OrderStatus.CONFIRMED);

        orderRepository.save(order);
        paymentRepository.save(payment);
        cartService.clearCart();

        return paymentMapper.paymentToPaymentDTO(payment);

    }
}
