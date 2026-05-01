package cinema.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class BookingPageDataResponse {
    private Long showtimeId;
    private String movieTitle;
    private String posterUrl;
    private String cinemaName;
    private String hallName;
    private String showDate;
    private String showTime;
    private String format;
    private List<SeatResponse> seats;
}