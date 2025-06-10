package pl.pjatk.Stepify.promotion.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
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
