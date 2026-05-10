package cinema.service.impl;

import cinema.dto.response.MovieResponse;
import cinema.entity.Movie;
import cinema.enums.MovieStatus;
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
        // 1. Lọc ra đúng các phim ĐANG CHIẾU (Sẽ lấy được 3 phim)
        List<Movie> movies = movieRepository.findByStatus(MovieStatus.NOW_SHOWING);

        // 2. SỬA LỖI Ở ĐÂY: Chỉ dùng biến "movies.stream()"
        return movies.stream().map(movie ->
                MovieResponse.builder()
                        .id(movie.getId())
                        .title(movie.getTitle())
                        .posterUrl(movie.getPosterUrl())
                        .bannerUrl(movie.getBannerUrl())
                        .description(movie.getDescription())
                        .slug(movie.getSlug())
                        .duration(movie.getDuration())
                        .releaseDate(movie.getReleaseDate())
                        .language(movie.getLanguage())
                        .trailerUrl(movie.getTrailerUrl())
                        .director(movie.getDirector())
                        .averageRating(movie.getAverageRating())
                        // Đoạn code này sẽ tự lấy danh sách ID, dịch ra tên (VD: "Hành động, Hài hước") và gửi cho React
                        .genre(movie.getGenres() != null && !movie.getGenres().isEmpty() ?
                                movie.getGenres().stream().map(g -> g.getName()).collect(Collectors.joining(", ")) :
                                "Đang cập nhật")
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
                // Đoạn code này sẽ tự lấy danh sách ID, dịch ra tên (VD: "Hành động, Hài hước") và gửi cho React
                .genre(movie.getGenres() != null && !movie.getGenres().isEmpty() ?
                        movie.getGenres().stream().map(g -> g.getName()).collect(Collectors.joining(", ")) :
                        "")
                // Thêm language, director... nếu trong DTO của anh có
                .build();
    }
    // Trong MovieServiceImpl.java
    @Override
    public List<MovieResponse> getComingSoonMovies() {
        // Lọc đúng phim SẮP CHIẾU
        List<Movie> movies = movieRepository.findByStatus(MovieStatus.COMING_SOON);

        // SỬA LỖI: Dùng chính biến 'movies'
        return movies.stream().map(movie ->
                MovieResponse.builder()
                        .id(movie.getId())
                        .title(movie.getTitle())
                        .posterUrl(movie.getPosterUrl())
                        .slug(movie.getSlug())
                        .releaseDate(movie.getReleaseDate())
                        // Đoạn code này sẽ tự lấy danh sách ID, dịch ra tên (VD: "Hành động, Hài hước") và gửi cho React
                        .genre(movie.getGenres() != null && !movie.getGenres().isEmpty() ?
                                movie.getGenres().stream().map(g -> g.getName()).collect(Collectors.joining(", ")) :
                                "Đang cập nhật")
                        .build()
        ).collect(Collectors.toList());
    }
}