package pl.pjatk.Stepify.product.dto.productSize;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductSizeUpdateDTO {

    private long id;
    @Positive(message = "Size must be greater than zero")
    private Double size;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

}
