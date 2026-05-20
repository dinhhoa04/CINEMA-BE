package cinema.service.impl;

import cinema.dto.request.LoginRequest;
import cinema.dto.request.RegisterRequest;
import cinema.dto.response.AuthResponse;
import cinema.entity.Role;
import cinema.entity.User;
import cinema.enums.RoleName;
import cinema.exception.AppException;
import cinema.repository.RoleRepository;
import cinema.repository.UserRepository;
import cinema.security.JwtUtil;
import cinema.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterRequest request) {
        // 1. Kiểm tra xem email đã tồn tại chưa
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(400, "Email này đã được sử dụng!");
        }

        // 2. Tìm Role CUSTOMER mặc định cho người dùng mới
        Role userRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseThrow(() -> new AppException(500, "Lỗi hệ thống: Không tìm thấy quyền CUSTOMER"));

        // 3. Tạo đối tượng User mới và lưu vào DB
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // Mã hóa mật khẩu
        user.setRole(userRole);
        user.setIsActive(true);

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. Xác thực tài khoản và mật khẩu thông qua AuthenticationManager
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new AppException(400, "Email hoặc mật khẩu không chính xác!");
        }

        // 2. Nếu đăng nhập đúng -> Lấy thông tin user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(404, "Không tìm thấy người dùng"));

        // 3. Tạo mã Token (Vé thông hành)
        String token = jwtUtil.generateToken(user.getEmail());

        // 4. Trả về kết quả
        return AuthResponse.builder()
                .accessToken(token)
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().getName().name())
                .permissions(user.getPermissions() != null ? user.getPermissions() : "")
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }
}