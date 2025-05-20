package pl.pjatk.Stepify.product.dto.productSize;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSizeCreateDTO {

    @Positive(message = "Size must be greater than zero")
    private double size;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

}
