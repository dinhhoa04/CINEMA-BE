package cinema.config;

import cinema.security.JwtAuthenticationFilter;
import cinema.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Cho phép dùng @PreAuthorize("hasRole('ADMIN')") trên Controller
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    // 1. Mã hóa mật khẩu (Không ai được lưu mật khẩu thô xuống DB)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Cấu hình Cung cấp xác thực
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Truyền thẳng userDetailsService vào ngay lúc khởi tạo (trong ngoặc tròn)
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

        // Dòng này vẫn giữ nguyên
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    // 3. Quản lý xác thực
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 4. Luật giao thông (Quy định đường nào được đi)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Mở cửa cho React gọi API
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF vì mình dùng JWT (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Cửa 1: Cho phép tất cả vào Đăng ký, Đăng nhập
                        .requestMatchers("/api/v1/auth/**", "/api/v1/movies/**").permitAll()
                        // Cửa 2: Cho phép khách vãng lai xem danh sách phim, rạp, suất chiếu
                        .requestMatchers(HttpMethod.GET, "/api/v1/movies/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/cinemas/**", "/api/v1/cities").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/showtimes/**").permitAll()
                        // Thêm "/api/v1/foods/**" vào danh sách cho phép (permitAll)
                        .requestMatchers(HttpMethod.GET,"/api/v1/auth/**", "/api/v1/system/**", "/api/v1/foods/**").permitAll()
                        // Cửa 3: Tất cả các đường còn lại (đặt vé, thanh toán, admin) ĐỀU PHẢI TRÌNH THẺ
                        .requestMatchers("/api/v1/payments/momo/ipn").permitAll()
                        .requestMatchers("/api/v1/payments/status/**").permitAll()
                        .anyRequest().authenticated()
                );

        // Gắn "Máy quét thẻ" lên trước bộ lọc kiểm tra mật khẩu mặc định của Spring
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 5. Cấu hình CORS cho Frontend React (Cổng 5173 của Vite hoặc 3000)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173","http://localhost:5174", "http://localhost:3000")); // Link Frontend của anh
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}