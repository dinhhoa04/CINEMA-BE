package cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes")
@Getter
@Setter
public class Showtime extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ N-1 với Movie
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    // Quan hệ N-1 với Hall
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(length = 50)
    private String language;

    @Column(length = 50)
    private String subtitle;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "is_active", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive = true;

    @Column(name = "total_seats")
    private Short totalSeats;

    @Column(name = "booked_seats")
    private Short bookedSeats = 0;
}