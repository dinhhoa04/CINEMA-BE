package cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"booking_id", "seat_id"})
})
@Getter
@Setter
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ N-1 với Booking
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // Quan hệ N-1 với Seat
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "seat_code", nullable = false, length = 10)
    private String seatCode;

    @Column(name = "seat_type_name", nullable = false, length = 50)
    private String seatTypeName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}