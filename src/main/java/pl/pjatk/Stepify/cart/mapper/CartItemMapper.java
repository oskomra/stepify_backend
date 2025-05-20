package pl.pjatk.Stepify.cart.mapper;

import org.mapstruct.*;
import pl.pjatk.Stepify.cart.dto.CartItemDTO;
import pl.pjatk.Stepify.cart.model.CartItem;
import pl.pjatk.Stepify.product.model.Product;
import pl.pjatk.Stepify.product.repository.ProductRepository;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.brandName", target = "brandName")
    @Mapping(source = "product.modelName", target = "modelName")
    @Mapping(target = "price", ignore = true)
    CartItemDTO mapCartItemToCartItemDTO(CartItem cartItem);

    @Mapping(target = "product", ignore = true) // We’ll set this manually
    @Mapping(target = "cart", ignore = true)    // Set this manually if needed later
    CartItem cartItemDTOToCartItem(CartItemDTO cartItemDTO, @Context ProductRepository productRepository);


    @AfterMapping
    default void setPriceFromProductColor(@MappingTarget CartItemDTO dto, CartItem cartItem) {
        if (cartItem.getProduct() != null && cartItem.getColor() != null) {
            cartItem.getProduct().getColors().stream()
                    .filter(pc -> pc.getColor().equalsIgnoreCase(cartItem.getColor()))
                    .findFirst()
                    .ifPresent(pc -> dto.setPrice(pc.getPrice()));
        }
    }

    @AfterMapping
    default void setProductFromRepository(@MappingTarget CartItem cartItem,
                                          CartItemDTO dto,
                                          @Context ProductRepository productRepository) {
        if (dto.getProductId() != 0) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product with id " + dto.getProductId() + " not found"));
            cartItem.setProduct(product);
        }
    }
}
