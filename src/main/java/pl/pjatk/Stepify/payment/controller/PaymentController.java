package pl.pjatk.Stepify.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.Stepify.payment.dto.PaymentDTO;
import pl.pjatk.Stepify.payment.dto.RequestPaymentDetailsDTO;
import pl.pjatk.Stepify.payment.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<PaymentDTO> processPayment(@PathVariable long id, @RequestBody RequestPaymentDetailsDTO requestPaymentDetailsDTO) {
        return ResponseEntity.ok(paymentService.processPayment(id, requestPaymentDetailsDTO));
    }
}
