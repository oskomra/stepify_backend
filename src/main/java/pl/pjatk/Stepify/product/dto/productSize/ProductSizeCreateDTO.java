package pl.pjatk.Stepify.product.dto.productSize;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSizeCreateDTO {

    @Min(value = 36, message = "Size must be at least 36")
    @Max(value = 48, message = "Size cannot be above 48")
    private Double size;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

}
