package cinema.controller;

import cinema.dto.request.ChangePasswordRequest;
import cinema.dto.request.UpdateProfileRequest;
import cinema.dto.response.ApiResponse;
import cinema.dto.response.AuthResponse;
import cinema.entity.Booking;
import cinema.entity.Role;
import cinema.entity.User;
import cinema.exception.AppException;
import cinema.repository.RoleRepository;
import cinema.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository; // Cực kỳ quan trọng: Để gọi danh sách Role
    private final PasswordEncoder passwordEncoder;

    private final cinema.repository.BookingRepository bookingRepository;

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new AppException(404, "Không tìm thấy người dùng"));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new AppException(400, "Mật khẩu cũ không chính xác!");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đổi mật khẩu thành công!", null));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<AuthResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new AppException(404, "Không tìm thấy người dùng"));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        userRepository.save(user);

        AuthResponse updatedUser = AuthResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().getName().name())
                .avatarUrl(user.getAvatarUrl())
                .build();
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật thông tin thành công!", updatedUser));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<java.util.Map<String, Object>>>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            List<java.util.Map<String, Object>> response = users.stream().map(user -> {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", user.getId());
                map.put("fullName", user.getFullName() != null ? user.getFullName() : "Khách hàng");
                map.put("email", user.getEmail());
                map.put("phone", user.getPhone());
                map.put("isActive", user.getIsActive() != null ? user.getIsActive() : true);

                // Đã đổi thành .toString() để chống sập dù Backend của bạn dùng String hay Enum
                String roleName = (user.getRole() != null && user.getRole().getName() != null)
                        ? user.getRole().getName().toString()
                        : "CUSTOMER";

                map.put("roleName", roleName);
                return map;
            }).toList();
            return ResponseEntity.ok(new ApiResponse<>(200, "Thành công", response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ApiResponse<>(500, "Lỗi Backend: " + e.getMessage(), null));
        }
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<java.util.Map<String, Object>>>> getAllRoles() {
        try {
            List<Role> roles = roleRepository.findAll();
            // Bóc tách thủ công để chặt đứt vòng lặp vô hạn
            List<java.util.Map<String, Object>> response = roles.stream().map(role -> {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", role.getId());
                map.put("name", role.getName());
                map.put("description", role.getDescription());
                return map;
            }).toList();

            return ResponseEntity.ok(new ApiResponse<>(200, "Success", response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ApiResponse<>(500, "Lỗi lấy Roles", null));
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody java.util.Map<String, Object> req) {
        User user = new User();
        user.setFullName((String) req.get("fullName"));
        user.setEmail((String) req.get("email"));
        user.setPasswordHash(passwordEncoder.encode((String) req.get("password")));
        user.setPhone((String) req.get("phone"));
        user.setGender(cinema.enums.Gender.valueOf((String) req.get("gender")));
        user.setDateOfBirth(java.time.LocalDate.parse((String) req.get("dateOfBirth")));

        Byte roleId = Byte.valueOf(req.get("roleId").toString());
        Role role = roleRepository.findById(roleId).orElseThrow();
        user.setRole(role);
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse<>(200, "Thêm mới thành công", user));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateRole(@PathVariable Long id, @RequestBody java.util.Map<String, Object> req) {
        User user = userRepository.findById(id).orElseThrow();
        Byte roleId = Byte.valueOf(req.get("roleId").toString());
        Role role = roleRepository.findById(roleId).orElseThrow();
        user.setRole(role);
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật quyền thành công", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> toggleLockUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse<>(200, user.getIsActive() ? "Đã MỞ KHÓA tài khoản" : "Đã KHÓA tài khoản", null));
    }

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> resetUserPassword(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
        User user = userRepository.findById(id).orElseThrow();
        String newPassword = request.get("newPassword");
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đã cấp lại mật khẩu", null));
    }

    // 4. Lấy lịch sử mua vé của người dùng
    // 4. Lấy lịch sử mua vé của người dùng (Bản cập nhật Title và Hall)
    @GetMapping("/{id}/bookings")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<java.util.Map<String, Object>>>> getUserBookings(@PathVariable Long id) {
        try {
            List<Booking> bookings = bookingRepository.findByUserId(id);

            List<java.util.Map<String, Object>> response = bookings.stream().map(booking -> {
                java.util.Map<String, Object> map = new java.util.HashMap<>();

                String movieTitle = "Không xác định";
                String cinemaName = "Không xác định";

                if (booking.getShowtime() != null) {
                    // Đổi từ getName() sang getTitle() theo đúng Entity Movie của bạn
                    if (booking.getShowtime().getMovie() != null) {
                        movieTitle = booking.getShowtime().getMovie().getTitle();
                    }

                    // Đổi từ getRoom() sang getHall() theo cấu trúc Showtime mới của bạn
                    if (booking.getShowtime().getHall() != null) {
                        // Lấy tên rạp thông qua Hall -> Cinema
                        if (booking.getShowtime().getHall().getCinema() != null) {
                            cinemaName = booking.getShowtime().getHall().getCinema().getName();
                        }
                    }
                }

                map.put("movieName", movieTitle);
                map.put("cinemaName", cinemaName);
                map.put("seatNames", "Đang xử lý ghế..."); // Sẽ map khi làm phần Ticket/Seat

                String bookingDate = booking.getCreatedAt() != null ?
                        booking.getCreatedAt().toLocalDate().toString() :
                        java.time.LocalDate.now().toString();
                map.put("bookingDate", bookingDate);
                map.put("totalPrice", booking.getFinalAmount());

                return map;
            }).toList();

            return ResponseEntity.ok(new ApiResponse<>(200, "Thành công", response));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ApiResponse<>(500, "Lỗi: " + e.getMessage(), null));
        }
    }
}