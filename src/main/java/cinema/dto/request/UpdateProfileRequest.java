package cinema.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    // SĐT có thể bỏ trống nếu không bắt buộc, nếu bắt buộc thì thêm @NotBlank
    private String phone;
}