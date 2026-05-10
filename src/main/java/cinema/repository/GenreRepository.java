package cinema.repository;

import cinema.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    // Hàm này giúp tìm Thể loại dựa trên chữ bạn gõ vào
    Optional<Genre> findByName(String name);
}