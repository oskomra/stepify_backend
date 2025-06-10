package pl.pjatk.Stepify.order.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pjatk.Stepify.payment.model.Payment;
import pl.pjatk.Stepify.promotion.model.AppliedPromotion;
import pl.pjatk.Stepify.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItems;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private double totalPrice;

    private LocalDateTime orderDate;

    @Embedded
    private ShippingAddress shippingAddress;

    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;

    @Enumerated(EnumType.STRING)
    private DeliveryCompany deliveryCompany;

    private String parcelLockerId;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppliedPromotion> appliedPromotions = new ArrayList<>();

    private double discountTotal;
    private double finalPrice;

    public Order(List<OrderItem> orderItems, double totalPrice) {
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
    }

    public void recalculateTotalPrice() {
        if (orderItems == null) {
            this.totalPrice = 0.0;
            this.discountTotal = 0.0;
            this.finalPrice = 0.0;
            return;
        }

        this.totalPrice = orderItems.stream()
                .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                .sum();

        this.discountTotal = appliedPromotions.stream()
                .mapToDouble(AppliedPromotion::getDiscountAmount)
                .sum();

        this.finalPrice = Math.max(0, this.totalPrice - this.discountTotal);
    }
}
