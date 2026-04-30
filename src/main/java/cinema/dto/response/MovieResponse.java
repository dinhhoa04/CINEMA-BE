package cinema.dto.response;

import lombok.Builder;
import lombok.Data;
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
}