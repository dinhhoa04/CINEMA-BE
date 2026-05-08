package cinema.dto.request;
import lombok.Data;
@Data
public class HallRequest {
    private Long cinemaId;
    private Long hallTypeId;
    private String name;
    private Byte totalRows;
    private Byte totalCols;
    private Boolean isActive;
}