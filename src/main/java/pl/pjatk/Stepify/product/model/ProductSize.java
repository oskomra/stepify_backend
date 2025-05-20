package pl.pjatk.Stepify.product.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_sizes")
public class ProductSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double size;

    private int stock;

    @ManyToOne
    @JoinColumn(name = "product_color_id", nullable = false)
    @JsonBackReference
    private ProductColor productColor;

}
