package pl.pjatk.Stepify.product.dto.productColor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeCreateDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductColorCreateDTO {

    @NotBlank(message = "Color is required")
    @Size(max = 100, message = "Color name must not exceed 100 characters")
    private String color;

    @Min(value = 1, message = "Price must be at least 1")
    @Max(value = 10000, message = "Price must not exceed 10,000")
    private double price;

    @NotEmpty(message = "At least 1 size is required")
    @Valid
    private List<ProductSizeCreateDTO> sizes;

    @NotNull
    private List<String> images;
}
