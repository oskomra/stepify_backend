package pl.pjatk.Stepify.cart.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import pl.pjatk.Stepify.product.model.Product;
import pl.pjatk.Stepify.product.model.ProductColor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference("product-cartItem")
    private Product product;

    private String color;

    private double size;

    private int quantity;

    private double price;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonBackReference("cart-cartItem")
    private Cart cart;

    public CartItem(Product product, String color, double size, int quantity, Cart cart) {
        this.product = product;
        this.color = color;
        this.size = size;
        this.quantity = quantity;
        this.cart = cart;
        this.price = product.getColors().stream()
                .filter(pc -> pc.getColor().equalsIgnoreCase(color))
                .findFirst()
                .map(ProductColor::getPrice)
                .orElse(0.0);
    }
}
