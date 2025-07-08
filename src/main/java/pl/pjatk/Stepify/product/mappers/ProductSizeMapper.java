package pl.pjatk.Stepify.product.mappers;

import org.mapstruct.*;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeCreateDTO;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeDTO;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeUpdateDTO;
import pl.pjatk.Stepify.product.model.ProductSize;


@Mapper(componentModel = "spring",
        uses = {ProductColorMapper.class, ProductMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductSizeMapper {


    ProductSizeDTO toDto(ProductSize productSize);

    ProductSize toEntity(ProductSizeCreateDTO productSizeCreateDTO);

    ProductSize toEntity(ProductSizeUpdateDTO productSizeUpdateDTO);

    @Mapping(target = "id", ignore = true)
    void patch(@MappingTarget ProductSize size, ProductSizeUpdateDTO dto);
}
