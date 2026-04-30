package cinema.repository;

import cinema.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Query("SELECT s FROM Showtime s JOIN s.hall h JOIN h.cinema c JOIN c.city ci JOIN c.cinemaChain cc " +
            "WHERE s.movie.id = :movieId " +
            "AND DATE(s.startTime) = :date " +
            "AND ci.name = :city " +
            "AND cc.name = :chainName " +
            "ORDER BY s.startTime ASC")
    List<Showtime> findShowtimesByFilters(
            @Param("movieId") Long movieId,
            @Param("date") LocalDate date,
            @Param("city") String city,
            @Param("chainName") String chainName);
}