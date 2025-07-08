package pl.pjatk.Stepify.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCancellationDTO {
    @NotBlank(message = "Cancellation reason is required")
    private String reason;
} 