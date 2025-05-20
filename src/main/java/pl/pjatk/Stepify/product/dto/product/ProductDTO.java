package pl.pjatk.Stepify.product.dto.product;

import lombok.*;
import pl.pjatk.Stepify.product.dto.productColor.ProductColorDTO;
import pl.pjatk.Stepify.product.model.Category;
import pl.pjatk.Stepify.product.model.Gender;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private long id;
    private String brandName;
    private String modelName;
    private Category category;
    private Gender gender;
    private String description;
    private List<ProductColorDTO> colors;
}
