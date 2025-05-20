package pl.pjatk.Stepify.cart.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import pl.pjatk.Stepify.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("cart-cartItem")
    private List<CartItem> cartItems;

    private double totalPrice;

    public Cart(User user) {
        this.user = user;
        this.cartItems = new ArrayList<>();
    }

    public void recalculateTotalPrice() {
        if (cartItems == null) {
            this.totalPrice = 0.0;
            return;
        }

        this.totalPrice = cartItems.stream()
                .mapToDouble(cartItems -> cartItems.getPrice() * cartItems.getQuantity())
                .sum();
    }
}
