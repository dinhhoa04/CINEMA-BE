package cinema.repository;
import cinema.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {}