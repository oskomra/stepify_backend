package pl.pjatk.Stepify.product.dto.productColor;

import lombok.*;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductColorDTO {

    private Long id;
    private String color;
    private double price;
    private List<ProductSizeDTO> sizes;
    private List<String> images;

}
