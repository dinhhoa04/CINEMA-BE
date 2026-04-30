package cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "seat_types")
@Getter
@Setter
public class SeatType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "color_code", length = 7)
    private String colorCode;

    @Column(name = "price_multiplier", precision = 3, scale = 2)
    private BigDecimal priceMultiplier = BigDecimal.ONE;
}