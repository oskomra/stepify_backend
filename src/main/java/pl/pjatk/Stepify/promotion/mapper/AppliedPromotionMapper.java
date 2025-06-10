package pl.pjatk.Stepify.promotion.mapper;

import org.mapstruct.Mapper;
import pl.pjatk.Stepify.promotion.dto.AppliedPromotionDTO;
import pl.pjatk.Stepify.promotion.model.AppliedPromotion;

@Mapper(componentModel = "spring")
public interface AppliedPromotionMapper {

    AppliedPromotionDTO toDTO(AppliedPromotion appliedPromotion);
    AppliedPromotion toEntity(AppliedPromotionDTO appliedPromotionDTO);

}
