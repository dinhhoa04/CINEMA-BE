package cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "halls", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cinema_id", "name"})
})
@Getter
@Setter
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ N-1 với Cinema
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    // Quan hệ N-1 với HallType
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hall_type_id", nullable = false)
    private HallType hallType;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "total_rows", nullable = false)
    private Byte totalRows;

    @Column(name = "total_cols", nullable = false)
    private Byte totalCols;

    @Column(name = "total_seats", nullable = false)
    private Short totalSeats;

    @Column(name = "is_active", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}