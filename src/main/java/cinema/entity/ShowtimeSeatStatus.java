package cinema.entity;

import cinema.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "showtime_seat_status", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"showtime_id", "seat_id"})
})
@Getter
@Setter
public class ShowtimeSeatStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ N-1 với Showtime
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    // Quan hệ N-1 với Seat
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    private SeatStatus status = SeatStatus.AVAILABLE;

    // Có thể rỗng nếu ghế đang AVAILABLE hoặc HOLDING
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "held_by_user")
    private User heldByUser;

    @Column(name = "held_until")
    private LocalDateTime heldUntil; // Thời hạn giữ ghế (VD: +10 phút)

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}