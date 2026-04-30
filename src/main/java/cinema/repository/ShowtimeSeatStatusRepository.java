package cinema.repository;
import cinema.entity.ShowtimeSeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowtimeSeatStatusRepository extends JpaRepository<ShowtimeSeatStatus, Long> {
}