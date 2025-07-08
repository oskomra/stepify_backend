package pl.pjatk.Stepify.order.mapper;

import org.mapstruct.*;
import pl.pjatk.Stepify.cart.mapper.CartItemMapper;
import pl.pjatk.Stepify.order.dto.OrderDTO;
import pl.pjatk.Stepify.order.dto.OrderSummaryDTO;
import pl.pjatk.Stepify.order.model.Order;
import pl.pjatk.Stepify.order.model.ShippingAddress;
import pl.pjatk.Stepify.user.model.Address;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "user.id",  target = "userId")
    OrderDTO mapOrderToOrderDTO(Order order);

    Order mapOrderDTOToOrder(OrderDTO orderDTO);

    @Mapping(source = "address.street", target = "street")
    @Mapping(source = "address.city", target = "city")
    @Mapping(source = "address.postalCode", target = "postalCode")
    @Mapping(source = "address.country", target = "country")
    ShippingAddress mapAddressToShippingAddress(Address address);

    @Mapping(source = "user.id",  target = "userId")
    OrderSummaryDTO mapOrderToOrderSummaryDTO(Order order);

    @AfterMapping
    default void setOrderItemReferences(@MappingTarget Order order) {
        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(orderItem -> orderItem.setOrder(order));
        }
    }
}
