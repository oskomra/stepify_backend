package pl.pjatk.Stepify.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

    private long id;
    private long productId;

    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name must not exceed 100 characters")
    private String brandName;

    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model name must not exceed 100 characters")
    private String modelName;

    @NotBlank(message = "Color is required")
    @Size(max = 100, message = "Model name must not exceed 100 characters")
    private String color;

    @NotBlank(message = "Size is required")
    @Size(max = 100, message = "Model name must not exceed 100 characters")
    private double size;

    @Min(0)
    private int quantity;

    @Min(0)
    private double price;
}
