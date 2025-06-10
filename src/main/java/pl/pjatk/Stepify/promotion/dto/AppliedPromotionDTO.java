package pl.pjatk.Stepify.promotion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppliedPromotionDTO {

    private Long id;
    private double discountAmount;
    private LocalDateTime appliedAt;
}
