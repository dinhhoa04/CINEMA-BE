package cinema.entity;

import cinema.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ N-1 với Booking
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // Quan hệ N-1 với PaymentMethod
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_id", length = 200)
    private String transactionId;

    @Column(name = "gateway_order_id", length = 200)
    private String gatewayOrderId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(length = 10)
    private String currency = "VND";

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    // MySQL JSON type map thành String trong Java
    @Column(name = "gateway_response", columnDefinition = "JSON")
    private String gatewayResponse;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "refund_amount", precision = 12, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_reason", columnDefinition = "TEXT")
    private String refundReason;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;
}