package pl.pjatk.Stepify.promotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pjatk.Stepify.promotion.model.AppliedPromotion;

@Repository
public interface AppliedPromotionRepository extends JpaRepository<AppliedPromotion, Integer> {
}
