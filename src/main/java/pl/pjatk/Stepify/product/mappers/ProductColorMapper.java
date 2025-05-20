package pl.pjatk.Stepify.product.mappers;

import org.mapstruct.*;
import pl.pjatk.Stepify.product.dto.productColor.ProductColorCreateDTO;
import pl.pjatk.Stepify.product.dto.productColor.ProductColorDTO;
import pl.pjatk.Stepify.product.dto.productColor.ProductColorUpdateDTO;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeUpdateDTO;
import pl.pjatk.Stepify.product.model.ProductColor;
import pl.pjatk.Stepify.product.model.ProductSize;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {ProductSizeMapper.class, ProductMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductColorMapper {

    ProductColorDTO toDto(ProductColor productColor);
    ProductColor toEntity(ProductColorCreateDTO productColorCreateDTO);
    ProductColor toEntity(ProductColorUpdateDTO productColorUpdateDTO);

    @Mapping(target = "sizes", ignore = true)
    @Mapping(target = "id", ignore = true)
    void patch(@MappingTarget ProductColor color, ProductColorUpdateDTO dto);

    @AfterMapping
    default void setSizeReference(@MappingTarget ProductColor color) {
        if (color.getSizes() != null) {
            color.getSizes().forEach(size -> size.setProductColor(color));
        }
    }

}
