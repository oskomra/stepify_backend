package pl.pjatk.Stepify.product.dto.productColor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeUpdateDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductColorUpdateDTO {

    private long id;
    private String color;
    private double price;
    private List<ProductSizeUpdateDTO> sizes;
    private List<String> images;
}
