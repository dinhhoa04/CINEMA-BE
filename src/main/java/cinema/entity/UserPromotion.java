package cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_promotions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "promotion_id"})
})
@Getter
@Setter
public class UserPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(name = "used_count")
    private Byte usedCount = 0;
}