package pl.pjatk.Stepify.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestColorAndSizeDTO {

    private String color;
    private double size;
}
