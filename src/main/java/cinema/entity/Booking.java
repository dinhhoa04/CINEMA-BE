package cinema.entity;

import cinema.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_code", nullable = false, unique = true, length = 20)
    private String bookingCode;

    // Quan hệ N-1 với User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Quan hệ N-1 với Showtime
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    // Quan hệ N-1 với Promotion (Có thể null nếu không dùng mã)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "service_fee", precision = 12, scale = 2)
    private BigDecimal serviceFee = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // Thời gian hết hạn giữ vé

    @Column(name = "qr_code_url", length = 500)
    private String qrCodeUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

    // Tự động sinh mã vé trước khi lưu
    @PrePersist
    protected void onCreate() {
        if (bookingCode == null) {
            bookingCode = UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        }
    }
}