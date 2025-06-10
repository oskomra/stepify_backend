package pl.pjatk.Stepify.payment.mapper;

import org.mapstruct.Mapper;
import pl.pjatk.Stepify.payment.dto.PaymentDTO;
import pl.pjatk.Stepify.payment.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentDTO paymentToPaymentDTO(Payment payment);
    Payment paymentDTOToPayment(PaymentDTO paymentDTO);
}
