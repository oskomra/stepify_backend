package pl.pjatk.Stepify.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pjatk.Stepify.product.model.ProductColor;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor, Long> {

    boolean existsProductColorByColor(String color);

    void deleteProductColorByColor(String color);
}
