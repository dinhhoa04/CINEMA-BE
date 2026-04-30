package cinema.entity;

import cinema.enums.MovieRated;
import cinema.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "title_en", length = 255)
    private String titleEn;

    @Column(nullable = false, unique = true, length = 300)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Short duration;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(length = 50)
    private String language;

    @Column(length = 50)
    private String subtitle;

    @Enumerated(EnumType.STRING)
    private MovieRated rated;

    @Enumerated(EnumType.STRING)
    private MovieStatus status = MovieStatus.COMING_SOON;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    @Column(name = "banner_url", length = 500)
    private String bannerUrl;

    @Column(name = "trailer_url", length = 500)
    private String trailerUrl;

    // Quan hệ N-1 với bảng countries
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(length = 200)
    private String director;

    @Column(name = "cast_members", columnDefinition = "TEXT")
    private String castMembers;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Column(name = "is_featured", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isFeatured = false;

    // Quan hệ N-N với bảng genres thông qua bảng trung gian movie_genres
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;
}