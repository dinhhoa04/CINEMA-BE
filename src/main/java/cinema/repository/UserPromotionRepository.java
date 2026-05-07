package cinema.repository;

import cinema.entity.UserPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserPromotionRepository extends JpaRepository<UserPromotion, Long> {
    // Thay vì đếm, chúng ta lấy nguyên dòng record đó ra để lấy biến usedCount của bạn
    Optional<UserPromotion> findByUserIdAndPromotionId(Long userId, Long promotionId);
}