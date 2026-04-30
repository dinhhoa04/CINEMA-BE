package cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_tokens")
@Getter
@Setter
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ N-1 với bảng users
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String token;

    @Column(name = "token_type", nullable = false)
    private String tokenType; // (Lưu ý: Có thể dùng Enum, nhưng để String cho lẹ vì chỉ check Logic)

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_used", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isUsed = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}