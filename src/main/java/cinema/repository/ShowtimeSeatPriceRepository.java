package cinema.repository;
import cinema.entity.ShowtimeSeatPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShowtimeSeatPriceRepository extends JpaRepository<ShowtimeSeatPrice, Long> {
    List<ShowtimeSeatPrice> findByShowtimeId(Long showtimeId);
}