package cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "showtime_seat_prices", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"showtime_id", "seat_type_id"})
})
@Getter
@Setter
public class ShowtimeSeatPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_type_id", nullable = false)
    private SeatType seatType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
}