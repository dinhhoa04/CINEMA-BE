package cinema.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class BookingRequest {
    private Long showtimeId;
    private List<Long> seatIds;
    private Map<Long, Integer> cart; // Map chứa FoodId -> Số lượng
    private BigDecimal finalTotal;   // Tổng tiền thanh toán
}