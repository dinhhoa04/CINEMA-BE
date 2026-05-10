package cinema.dto.request;
import cinema.enums.MovieRated;
import cinema.enums.MovieStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MovieRequest {
    private String title;
    private String titleEn;
    private String slug;
    private String description;
    private Short duration;
    private LocalDate releaseDate;
    private String language;
    private MovieRated rated;
    private MovieStatus status; // NOW_SHOWING, COMING_SOON
    private String posterUrl;
    private String bannerUrl;
    private String trailerUrl;
    private Boolean isFeatured;
    private String director;
    private String castMembers;
    private BigDecimal averageRating;
    private Long countryId;
    private String genre;


}