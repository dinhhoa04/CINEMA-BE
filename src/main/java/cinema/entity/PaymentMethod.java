package cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payment_methods")
@Getter
@Setter
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "is_active", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive = true;
}