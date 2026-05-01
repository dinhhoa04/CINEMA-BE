package cinema.repository;
import cinema.entity.BookingFoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BookingFoodItemRepository extends JpaRepository<BookingFoodItem, Long> {}