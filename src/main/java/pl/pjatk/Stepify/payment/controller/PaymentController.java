package pl.pjatk.Stepify.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.pjatk.Stepify.payment.dto.PaymentDTO;
import pl.pjatk.Stepify.payment.service.PaymentService;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/payment/{id}")
    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(id));
    }
}
