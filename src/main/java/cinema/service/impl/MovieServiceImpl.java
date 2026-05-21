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
        List<Movie> movies = movieRepository.findByStatus(MovieStatus.NOW_SHOWING);

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
                .language(movie.getLanguage())
                .director(movie.getDirector())
                .averageRating(movie.getAverageRating())
                .genre(movie.getGenres() != null && !movie.getGenres().isEmpty() ?
                        movie.getGenres().stream().map(g -> g.getName()).collect(Collectors.joining(", ")) :
                        "")
                .build();
    }

    @Override
    public List<MovieResponse> getComingSoonMovies() {
        List<Movie> movies = movieRepository.findByStatus(MovieStatus.COMING_SOON);

        // ✅ ĐÃ SỬA: Copy nguyên phần map dữ liệu đầy đủ từ hàm NOW_SHOWING xuống
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
                        .genre(movie.getGenres() != null && !movie.getGenres().isEmpty() ?
                                movie.getGenres().stream().map(g -> g.getName()).collect(Collectors.joining(", ")) :
                                "Đang cập nhật")
                        .build()
        ).collect(Collectors.toList());
    }
}