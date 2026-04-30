package cinema.controller;

import cinema.dto.request.LoginRequest;
import cinema.dto.request.RegisterRequest;
import cinema.dto.response.ApiResponse;
import cinema.dto.response.AuthResponse;
import cinema.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // API 1: Đăng ký tài khoản
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);

        ApiResponse<Void> response = new ApiResponse<>();
        response.setMessage("Đăng ký tài khoản thành công!");

        return ResponseEntity.ok(response);
    }

    // API 2: Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);

        ApiResponse<AuthResponse> response = new ApiResponse<>();
        response.setMessage("Đăng nhập thành công!");
        response.setData(authResponse);

        return ResponseEntity.ok(response);
    }
}