package cinema.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cinema_chains")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CinemaChain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "logo_url")
    private String logoUrl;
    @Column(columnDefinition = "TINYINT")
    private Integer status;
}