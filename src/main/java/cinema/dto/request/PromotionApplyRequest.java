package cinema.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PromotionApplyRequest {
    private String code;
    private Long showtimeId;
    private BigDecimal orderTotal; // Cần gửi thêm Tổng tiền từ React lên để Backend tính giảm % hoặc check Đơn tối thiểu
}