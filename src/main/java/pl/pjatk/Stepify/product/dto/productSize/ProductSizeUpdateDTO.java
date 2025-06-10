package pl.pjatk.Stepify.product.dto.productSize;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductSizeUpdateDTO {

    private long id;
    @Min(value = 36, message = "Size must be at least 36")
    @Max(value = 48, message = "Size cannot be above 48")
    private Double size;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

}
