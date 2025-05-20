package pl.pjatk.Stepify.cart.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import pl.pjatk.Stepify.cart.dto.CartDTO;
import pl.pjatk.Stepify.cart.model.Cart;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {

    CartDTO mapCartToCartDTO(Cart cart);
    Cart mapCartDTOToCart(CartDTO cartDTO);

    @AfterMapping
    default void setCartItemReferences(@MappingTarget Cart cart) {
        if (cart.getCartItems() != null) {
            cart.getCartItems().forEach(cartItem -> cartItem.setCart(cart));
        }
    }
}
