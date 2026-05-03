package cinema.dto.response;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MovieShowtimeResponse {
    private Long movieId;
    private String movieTitle;
    private String posterUrl;
    private String rated;
    private List<ShowtimeDetailResponse> times;
}