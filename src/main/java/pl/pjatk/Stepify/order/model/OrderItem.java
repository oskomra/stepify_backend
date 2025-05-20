package pl.pjatk.Stepify.order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String brandName;
    private String modelName;
    private String color;
    private double size;
    private int quantity;
    private double price;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    public OrderItem(String brandName, String modelName, String color, double size, int quantity, double price, Order order) {
        this.brandName = brandName;
        this.modelName = modelName;
        this.color = color;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.order = order;
    }
}
