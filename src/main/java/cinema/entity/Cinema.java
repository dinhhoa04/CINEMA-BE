package cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "cinemas")
@Getter
@Setter
public class Cinema extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 200)
    private String slug;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(length = 15)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "map_url", length = 500)
    private String mapUrl;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "is_active", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive = true;

    // 1. Quan hệ N-1 với bảng cities (Đã có từ trước, giữ nguyên)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    // 2. Quan hệ N-1 với bảng cinema_chains (Cụm rạp mới thêm)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chain_id")
    private CinemaChain cinemaChain;
}