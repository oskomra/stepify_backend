package pl.pjatk.Stepify.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pjatk.Stepify.payment.model.PaymentMethod;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {

    private PaymentMethod method;
    private double amount;
    private LocalDateTime paymentDate;
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;
    private String blikCode;
}
