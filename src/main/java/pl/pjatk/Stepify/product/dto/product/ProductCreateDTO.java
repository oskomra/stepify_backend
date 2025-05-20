package pl.pjatk.Stepify.product.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import pl.pjatk.Stepify.product.dto.productColor.ProductColorCreateDTO;
import pl.pjatk.Stepify.product.model.Category;
import pl.pjatk.Stepify.product.model.Gender;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {

    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name must not exceed 100 characters")
    private String brandName;

    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model name must not exceed 100 characters")
    private String modelName;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Size(max = 1500, message = "Description must not exceed 500 characters")
    @NotBlank(message = "Description is required")
    private String description;

    @NotEmpty(message = "Color is required")
    @Valid
    private List<ProductColorCreateDTO> colors;
}
