package pl.pjatk.Stepify.cart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    private long id;

    @Valid
    private List<CartItemDTO> cartItems;

    @Min(0)
    private double totalPrice;
}
