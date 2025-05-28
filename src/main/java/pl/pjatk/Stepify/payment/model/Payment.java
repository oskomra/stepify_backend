package pl.pjatk.Stepify.payment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pjatk.Stepify.order.model.Order;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
    private double amount;
    private LocalDateTime paymentDate;
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;
    private String blikCode;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

}
