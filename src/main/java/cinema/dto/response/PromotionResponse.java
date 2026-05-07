package cinema.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PromotionResponse {
    private BigDecimal discountAmount;
    private String message;
    private String imageUrl;
}