package pl.pjatk.Stepify.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pjatk.Stepify.exception.ResourceNotFoundException;
import pl.pjatk.Stepify.exception.UnauthorizedAccessException;
import pl.pjatk.Stepify.payment.dto.PaymentDTO;
import pl.pjatk.Stepify.payment.mapper.PaymentMapper;
import pl.pjatk.Stepify.payment.model.Payment;
import pl.pjatk.Stepify.payment.repository.PaymentRepository;
import pl.pjatk.Stepify.user.model.User;
import pl.pjatk.Stepify.user.service.UserService;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final UserService userService;


    public PaymentDTO getPaymentByOrderId(Long orderId) {
        User user = userService.getCurrentUser();
        Payment payment;

        if (orderId.equals(user.getId())) {
            payment = paymentRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        } else {
            throw new UnauthorizedAccessException("Unauthorized access");
        }
        return paymentMapper.paymentToPaymentDTO(payment);
    }
}
