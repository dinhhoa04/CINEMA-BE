package cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "food_categories")
@Getter
@Setter
public class FoodCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "sort_order")
    private Byte sortOrder = 0;
}