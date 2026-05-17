package cinema.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingAdminResponse {
    private Long id;
    private String bookingCode;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String movieTitle;
    private String cinemaName;
    private String hallName;
    private String showTime;
    private String seatNames;
    private BigDecimal finalAmount;
    private String status;
    private String paymentMethod;
    private LocalDateTime bookingTime;
    private String qrCodeUrl;
}