package pl.pjatk.Stepify.promotion.mapper;

import org.mapstruct.Mapper;
import pl.pjatk.Stepify.promotion.dto.PromotionDTO;
import pl.pjatk.Stepify.promotion.model.Promotion;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    PromotionDTO mapPromotionToPromotionDTO(Promotion promotion);

    Promotion mapPromotionDTOToPromotion(PromotionDTO promotionDTO);

    List<PromotionDTO> mapPromotionsToPromotionDTO(List<Promotion> promotions);


}
