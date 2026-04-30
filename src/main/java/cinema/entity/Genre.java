package cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "genres")
@Getter
@Setter
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id; // Dùng Short vì SQL của anh thiết kế là SMALLINT

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}