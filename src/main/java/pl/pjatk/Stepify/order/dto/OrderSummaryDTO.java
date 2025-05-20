package pl.pjatk.Stepify.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class OrderSummaryDTO {

    private long id;
    private long userId;
    private List<OrderItemDTO> orderItems;
    private double totalPrice;
    private AddressDTO shippingAddress;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private DeliveryMethod deliveryMethod;
    private DeliveryCompany deliveryCompany;
}
