package cinema.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalTime;

@Data
@Builder
public class ShowtimeDetailResponse {
    private Long showtimeId;
    private String startTime; // Ví dụ: "14:30"
    private String format;    // Ví dụ: "2D Phụ đề Việt"
}