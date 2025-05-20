package pl.pjatk.Stepify.product.mappers;


import org.mapstruct.*;
import pl.pjatk.Stepify.product.dto.product.ProductCreateDTO;
import pl.pjatk.Stepify.product.dto.product.ProductDTO;
import pl.pjatk.Stepify.product.dto.product.ProductUpdateDTO;
import pl.pjatk.Stepify.product.dto.productColor.ProductColorDTO;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeDTO;
import pl.pjatk.Stepify.product.model.Product;
import pl.pjatk.Stepify.product.model.ProductFilter;


@Mapper(
        componentModel = "spring",
        uses = {ProductColorMapper.class, ProductSizeMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    ProductDTO toDto(Product product);

    Product toEntity(ProductCreateDTO productCreateDTO);

    Product toEntity(ProductUpdateDTO productUpdateDTO);

    @Mapping(target = "colors", ignore = true)
    @Mapping(target = "id", ignore = true)
    void patch(@MappingTarget Product product, ProductUpdateDTO dto);

    default ProductDTO toFilteredDto(Product product, ProductFilter filter) {
        ProductDTO dto = toDto(product); // basic mapping from MapStruct

        dto.setColors(
                product.getColors().stream()
                        .filter(color -> {
                            boolean colorMatch = filter.getColors() == null || filter.getColors().isEmpty() || filter.getColors().contains(color.getColor());
                            boolean sizeMatch = filter.getSizes() == null || filter.getSizes().isEmpty() ||
                                    color.getSizes().stream().anyMatch(size -> filter.getSizes().contains(size.getSize()));
                            return colorMatch && sizeMatch;
                        })
                        .map(color -> {
                            // Filter sizes
                            var matchingSizes = color.getSizes().stream()
                                    .filter(size -> filter.getSizes() == null || filter.getSizes().isEmpty() || filter.getSizes().contains(size.getSize()))
                                    .map(size -> new ProductSizeDTO(size.getId(), size.getSize(), size.getStock()))
                                    .toList();

                            return new ProductColorDTO(
                                    color.getId(),
                                    color.getColor(),
                                    color.getPrice(),
                                    matchingSizes,
                                    color.getImages()
                            );
                        })
                        .toList()
        );

        return dto;
    }


    @AfterMapping
    default void setColorReferences(@MappingTarget Product product) {
        if (product.getColors() != null) {
            product.getColors().forEach(color -> color.setProduct(product));
        }
    }

}

