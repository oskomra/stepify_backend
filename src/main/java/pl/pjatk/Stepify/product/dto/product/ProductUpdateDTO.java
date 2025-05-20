package pl.pjatk.Stepify.product.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import pl.pjatk.Stepify.product.dto.productColor.ProductColorUpdateDTO;
import pl.pjatk.Stepify.product.model.Category;
import pl.pjatk.Stepify.product.model.Gender;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductUpdateDTO {

    private long id;
    private String brandName;
    private String modelName;
    private Category category;
    private Gender gender;
    private String description;
    private List<ProductColorUpdateDTO> colors;
}
