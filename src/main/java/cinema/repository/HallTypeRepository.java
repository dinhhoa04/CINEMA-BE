package cinema.repository;
import cinema.entity.HallType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HallTypeRepository extends JpaRepository<HallType, Long> {
}
