package pl.pjatk.Stepify.promotion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pjatk.Stepify.promotion.model.PromotionType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDTO {

    private int id;

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Description is required")
    private String description;

    @NotNull(message = "Promotion type is required")
    private PromotionType promotionType;

    private double value;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
    private String code;
    private Integer usageLimit;
    private Integer usageCount;
    private Double minimumOrderValue;
    private boolean stackable;
}
