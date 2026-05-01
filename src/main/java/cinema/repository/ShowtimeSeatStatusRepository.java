package cinema.repository;
import cinema.entity.ShowtimeSeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShowtimeSeatStatusRepository extends JpaRepository<ShowtimeSeatStatus, Long> {
    List<ShowtimeSeatStatus> findByShowtimeId(Long showtimeId);
    Optional<ShowtimeSeatStatus> findByShowtimeIdAndSeatId(Long showtimeId, Long seatId);
}