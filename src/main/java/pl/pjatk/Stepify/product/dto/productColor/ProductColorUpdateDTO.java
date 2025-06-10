package pl.pjatk.Stepify.product.dto.productColor;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeUpdateDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductColorUpdateDTO {

    private long id;

    @NotNull()
    private String color;
    @Positive(message = "Price cannot be negative")
    private double price;
    @NotNull(message = "")
    private List<ProductSizeUpdateDTO> sizes;
    private List<String> images;
}
