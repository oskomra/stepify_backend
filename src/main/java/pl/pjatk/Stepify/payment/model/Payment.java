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
    private PaymentStatus paymentStatus;
    private double amount;
    private LocalDateTime paymentDate;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private String transactionId;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public Payment(PaymentMethod paymentMethod, double amount, PaymentStatus paymentStatus, Order order) {
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.order = order;
        this.paymentStatus = paymentStatus;
    }


}
