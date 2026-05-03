package cinema.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class BookingHistoryResponse {
    private String bookingCode;
    private String movieTitle;
    private String posterUrl;
    private String cinemaName;
    private String hallName;
    private String showDate;
    private String showTime;
    private String seatNames; // VD: "A1, A2, VIP1"
    private BigDecimal totalAmount;
    private String status;
    private String bookingDate;
}