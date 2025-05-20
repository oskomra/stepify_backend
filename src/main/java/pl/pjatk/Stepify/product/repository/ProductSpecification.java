package pl.pjatk.Stepify.product.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import pl.pjatk.Stepify.product.model.Product;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import pl.pjatk.Stepify.product.model.ProductColor;
import pl.pjatk.Stepify.product.model.ProductFilter;
import pl.pjatk.Stepify.product.model.ProductSize;

public class ProductSpecification {

    public static Specification<Product> filterByCriteria(ProductFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            addPredicateIfNotEmpty(root, criteriaBuilder, "brandName", filter.getBrandNames(), predicates);
            addPredicateIfNotEmpty(root, criteriaBuilder, "modelName", filter.getModelNames(), predicates);
            addPredicateIfNotEmpty(root, criteriaBuilder, "category", filter.getCategories(), predicates);
            addPredicateIfNotEmpty(root, criteriaBuilder, "gender", filter.getGenders(), predicates);

            if (filter.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            Join<Product, ProductColor> colorJoin = root.join("colors");
            if (filter.getColors() != null && !filter.getColors().isEmpty()) {
                predicates.add(colorJoin.get("color").in(filter.getColors()));
            }

            if (filter.getSizes() != null && !filter.getSizes().isEmpty()) {
                Join<ProductColor, ProductSize> sizeJoin = colorJoin.join("sizes");
                predicates.add(sizeJoin.get("size").in(filter.getSizes()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static <T> void addPredicateIfNotEmpty(Root<Product> root, CriteriaBuilder criteriaBuilder, String fieldName,
                                                   List<T> values, List<Predicate> predicates) {
        if (values != null && !values.isEmpty()) {
            predicates.add(root.get(fieldName).in(values));
        }
    }
}


