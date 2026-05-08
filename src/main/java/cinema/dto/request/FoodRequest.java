package cinema.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FoodRequest {
    private Long categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Boolean isAvailable;
    private Byte sortOrder; // Kiểu Byte khớp hoàn toàn với TINYINT trong DB
}