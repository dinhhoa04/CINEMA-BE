package cinema.dto.request;
import lombok.Data;
@Data
public class CinemaRequest {
    private String name;
    private String slug;
    private String address;
    private String phone;
    private String email;
    private String logoUrl;
    private Long cityId;
    private Long chainId;
    private Boolean isActive;
}