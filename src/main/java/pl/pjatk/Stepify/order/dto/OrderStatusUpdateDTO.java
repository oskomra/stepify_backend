package pl.pjatk.Stepify.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pjatk.Stepify.order.model.OrderStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusUpdateDTO {
    @NotNull(message = "Status is required")
    private OrderStatus status;
} 