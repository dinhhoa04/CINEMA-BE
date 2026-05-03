package cinema.dto.response;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatDetailDTO {
    private Long seatId;
    private String name;       // "A1", "B2"
    private String rowName;    // "A"
    private Integer colIndex;  // 1
    private Integer typeId;    // 1 (Standard), 2 (VIP)
    private String status;     // "AVAILABLE", "HOLDING", "BOOKED"
    private Double price;      // 80000.0
}