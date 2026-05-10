package cinema.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class MovieResponse {
    private Long id;
    private String title;
    private String posterUrl;
    private Short duration;
    private LocalDate releaseDate;
    private String language;
    private String trailerUrl;
    private String slug;
    private String description;
    private String bannerUrl;
    private String director;
    private BigDecimal averageRating; // Dùng Double nếu DB là Double
    private String genre; // Tạm dùng String cho Thể loại

}