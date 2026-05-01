package cinema.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class SeatResponse {
    private Long id;
    private String name;     // VD: A1
    private String row;      // VD: A
    private Byte col;        // VD: 1
    private String type;     // STANDARD, VIP, COUPLE
    private BigDecimal price;
    private String status;   // AVAILABLE, HOLDING, BOOKED
}