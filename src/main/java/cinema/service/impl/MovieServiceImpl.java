package cinema.service.impl;

import cinema.dto.response.MovieResponse;
import cinema.entity.Movie;
import cinema.repository.MovieRepository;
import cinema.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public List<MovieResponse> getNowShowingMovies() {
        // Lấy tất cả phim từ DB và biến đổi (Map) sang định dạng DTO
        return movieRepository.findAll().stream().map(movie ->
                MovieResponse.builder()
                        .id(movie.getId())
                        .title(movie.getTitle())
                        .posterUrl(movie.getPosterUrl())
                        .duration(movie.getDuration())
                        .releaseDate(movie.getReleaseDate())
                        .language(movie.getLanguage())
                        .trailerUrl(movie.getTrailerUrl())
                        .build()
        ).collect(Collectors.toList());
    }
    @Override
    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim với ID: " + id));

        // Dùng Builder Pattern để tạo Response (Sẽ không còn lỗi đỏ nữa)
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .slug(movie.getSlug())
                .description(movie.getDescription())
                .posterUrl(movie.getPosterUrl())
                .bannerUrl(movie.getBannerUrl())
                .trailerUrl(movie.getTrailerUrl())
                .duration(movie.getDuration())
                .releaseDate(movie.getReleaseDate())
                // Thêm language, director... nếu trong DTO của anh có
                .build();
    }
    // Trong MovieServiceImpl.java
    @Override
    public List<MovieResponse> getComingSoonMovies() {
        return movieRepository.findByStatus(cinema.enums.MovieStatus.COMING_SOON).stream().map(movie ->
                MovieResponse.builder()
                        .id(movie.getId())
                        .title(movie.getTitle())
                        .posterUrl(movie.getPosterUrl())
                        .slug(movie.getSlug())
                        .releaseDate(movie.getReleaseDate())
                        .build()
        ).collect(Collectors.toList());
    }
}