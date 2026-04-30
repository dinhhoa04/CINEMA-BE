package cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"hall_id", "row_label", "col_number"})
})
@Getter
@Setter
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ N-1 với Hall
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    // Quan hệ N-1 với SeatType
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seat_type_id", nullable = false)
    private SeatType seatType;

    @Column(name = "row_label", nullable = false, length = 5)
    private String rowLabel;

    @Column(name = "col_number", nullable = false)
    private Byte colNumber;

    @Column(name = "seat_code", nullable = false, length = 10)
    private String seatCode;

    @Column(name = "is_active", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive = true;

    @Column(name = "position_x")
    private Short positionX;

    @Column(name = "position_y")
    private Short positionY;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}