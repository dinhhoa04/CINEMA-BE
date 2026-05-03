package cinema.dto.response;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class BookingDataResponse {
    // Thông tin header bên phải
    private String movieTitle;
    private String cinemaName;
    private String hallName;
    private String showDate;
    private String startTime;
    private String posterUrl;

    // Ma trận ghế bên trái
    private List<SeatDetailDTO> seats;
}