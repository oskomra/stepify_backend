package pl.pjatk.Stepify.product.dto.productSize;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSizeDTO {

    private long id;
    private double size;
    private int stock;
}
