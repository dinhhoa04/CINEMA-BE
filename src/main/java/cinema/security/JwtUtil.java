package cinema.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Khóa bí mật (Chuỗi ngẫu nhiên >= 256bit). Thực tế nên để trong application.yaml
    private final String SECRET = "DayLaKhoaBiMatCuaDuAnCinemaDemoHoa2026BaoMatTuyetDoi";

    // Thời gian sống của Token: 1 ngày (86400000 ms)
    private final long EXPIRATION = 86400000;

    // Lấy Key từ chuỗi bí mật
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Hàm tạo Token khi Login thành công
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    // Hàm giải mã Token để lấy Email
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Hàm kiểm tra Token còn xài được không hay là hàng fake
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}