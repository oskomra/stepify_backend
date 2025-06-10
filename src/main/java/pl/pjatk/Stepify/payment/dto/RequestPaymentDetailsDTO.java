package pl.pjatk.Stepify.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestPaymentDetailsDTO {

    private String creditCardHolderName;
    private String creditCardHolderLastName;
    private String creditCardNumber;
    private String creditCardExpiryDate;
    private String creditCardCVV;
    private String blikCode;
}
