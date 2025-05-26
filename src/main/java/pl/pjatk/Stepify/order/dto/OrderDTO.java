package pl.pjatk.Stepify.order.dto;

import lombok.*;
import pl.pjatk.Stepify.order.model.DeliveryCompany;
import pl.pjatk.Stepify.order.model.DeliveryMethod;
import pl.pjatk.Stepify.order.model.OrderStatus;
import pl.pjatk.Stepify.user.dto.AddressDTO;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private long id;
    private long userId;
    private List<OrderItemDTO> orderItems;
    private double totalPrice;
    private AddressDTO address;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private DeliveryMethod deliveryMethod;
    private DeliveryCompany deliveryCompany;

}
