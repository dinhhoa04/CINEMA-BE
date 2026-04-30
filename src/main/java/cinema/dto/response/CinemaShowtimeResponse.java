package cinema.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CinemaShowtimeResponse {
    private Long cinemaId;
    private String cinemaName;
    private String address;
    private List<ShowtimeDetailResponse> times; // Danh sách các giờ chiếu của rạp này
}