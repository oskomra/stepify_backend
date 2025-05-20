package pl.pjatk.Stepify.order.mapper;


import org.mapstruct.Mapper;
import pl.pjatk.Stepify.order.dto.OrderItemDTO;
import pl.pjatk.Stepify.order.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {


    OrderItemDTO mapOrderItemToOrderItemDTO(OrderItem orderItem);

    OrderItem cartOrderDTOToOrderItem(OrderItemDTO orderItemDTO);
}
