package pl.pjatk.Stepify.product.model;

import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductFilter {

    private List<String> brandNames;
    private List<String> modelNames;
    private List<Category> categories;
    private List<Gender> genders;
    private List<Double> sizes;
    private List<String> colors;
    private Double minPrice;
    private Double maxPrice;
}
