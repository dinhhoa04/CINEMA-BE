package cinema.service;

import cinema.dto.request.LoginRequest;
import cinema.dto.request.RegisterRequest;
import cinema.dto.response.AuthResponse;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}