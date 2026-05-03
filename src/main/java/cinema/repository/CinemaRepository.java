package cinema.repository;
import cinema.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    // Thêm dòng này:
    List<Cinema> findByCinemaChainId(Long chainId);
}