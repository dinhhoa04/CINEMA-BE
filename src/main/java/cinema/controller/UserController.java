package cinema.controller;

import cinema.dto.request.ChangePasswordRequest;
import cinema.dto.response.ApiResponse;
import cinema.entity.User;
import cinema.exception.AppException;
import cinema.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import cinema.dto.request.UpdateProfileRequest;
import cinema.dto.response.AuthResponse;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request, Principal principal) {

        // 1. Tìm user đang đăng nhập bằng Email (Principal)
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new AppException(404, "Không tìm thấy người dùng"));

        // 2. Kiểm tra mật khẩu cũ có khớp không
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new AppException(400, "Mật khẩu cũ không chính xác!");
        }

        // 3. Mã hóa và lưu mật khẩu mới
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        ApiResponse<Void> response = new ApiResponse<>();
        response.setMessage("Đổi mật khẩu thành công!");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<AuthResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request, Principal principal) {

        // 1. Tìm user đang đăng nhập
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new AppException(404, "Không tìm thấy người dùng"));

        // 2. Cập nhật thông tin
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        userRepository.save(user);

        // 3. Đóng gói lại thông tin mới để Frontend cập nhật Zustand
        AuthResponse updatedUser = AuthResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().getName().name())
                .avatarUrl(user.getAvatarUrl())
                // accessToken không thay đổi nên Frontend sẽ tự giữ nguyên
                .build();

        ApiResponse<AuthResponse> response = new ApiResponse<>();
        response.setMessage("Cập nhật thông tin thành công!");
        response.setData(updatedUser);

        return ResponseEntity.ok(response);
    }
}