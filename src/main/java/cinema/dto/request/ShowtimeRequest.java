package cinema.dto.request;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShowtimeRequest {
    private Long movieId;
    private Long hallId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String language;
    private BigDecimal basePrice;
    private Boolean isActive;
    private String subtitle; // Thêm trường phụ đề
}