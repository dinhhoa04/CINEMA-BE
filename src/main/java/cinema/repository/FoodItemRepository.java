package cinema.repository; // Đổi lại package cho khớp project của anh

import cinema.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    // Câu lệnh ma thuật tự động dịch thành: SELECT * FROM food_items WHERE is_available = 1
    List<FoodItem> findByIsAvailableTrue();
}