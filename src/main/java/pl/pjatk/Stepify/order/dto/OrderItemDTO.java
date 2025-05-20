package pl.pjatk.Stepify.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {

    private long id;
    private String brandName;
    private String modelName;
    private String color;
    private double size;
    private int quantity;
    private double price;
}
