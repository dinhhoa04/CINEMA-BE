package cinema.repository;
import cinema.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByStatus(cinema.enums.MovieStatus status);
    Optional<Movie> findBySlug(String slug);
    List<Movie> findByStatus(String status);
}