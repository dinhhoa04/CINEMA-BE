package cinema.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String fullName;
    private String avatarUrl;
    private String role;
    private String email;
    private String phone;
    private String permissions;
}