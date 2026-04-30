package cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hall_types")
@Getter
@Setter
public class HallType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 255)
    private String description;
}