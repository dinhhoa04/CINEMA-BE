package cinema.controller;

import cinema.dto.response.ApiResponse;
import cinema.dto.response.MovieResponse;
import cinema.entity.Movie;
import cinema.repository.GenreRepository;
import cinema.repository.MovieRepository;
import cinema.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MovieController {

    private final MovieService movieService;
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    // API: Lấy danh sách phim đang chiếu
    @GetMapping("/now-showing")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getNowShowing() {
        ApiResponse<List<MovieResponse>> response = new ApiResponse<>();
        response.setMessage("Lấy danh sách phim thành công");
        response.setData(movieService.getNowShowingMovies());
        return ResponseEntity.ok(response);
    }

    // API: Lấy danh sách phim sắp chiếu
    @GetMapping("/coming-soon")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getComingSoon() {
        ApiResponse<List<MovieResponse>> response = new ApiResponse<>();
        response.setMessage("Lấy danh sách phim sắp chiếu thành công");
        response.setData(movieService.getComingSoonMovies());
        return ResponseEntity.ok(response);
    }

    // 1. API lấy phim theo ID (Dành cho Admin - Chỉ nhận vào SỐ)
    // Ký hiệu \\d+ báo cho Spring Boot biết URL này chỉ chạy khi biến là một con số (VD: /movies/1)
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieById(@PathVariable Long id) {
        ApiResponse<MovieResponse> response = new ApiResponse<>();
        response.setMessage("Lấy chi tiết phim thành công");
        response.setData(movieService.getMovieById(id));
        return ResponseEntity.ok(response);
    }

    // 2. API lấy phim theo Slug (Dành cho Khách hàng - Nhận vào CHỮ)
    // Ký hiệu [a-zA-Z0-9-]+ cho phép nhận các ký tự chữ, số và dấu gạch ngang (VD: /movies/lat-mat-7)
    @GetMapping("/{slug:[a-zA-Z0-9-]+}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieBySlug(@PathVariable String slug) {
        // Bước 1: Tìm phim bằng slug trong DB để lấy ra ID
        Movie movie = movieRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim với Slug: " + slug));

        // Bước 2: Tái sử dụng hàm getMovieById của Service để trả về đúng định dạng DTO (chống lỗi đệ quy 500)
        ApiResponse<MovieResponse> response = new ApiResponse<>();
        response.setMessage("Lấy chi tiết phim thành công");
        response.setData(movieService.getMovieById(movie.getId()));

        return ResponseEntity.ok(response);
    }

    // --- API CHO ADMIN (CRUD MOVIE) ---
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Movie>>> getAllMoviesAdmin() {
        ApiResponse<List<Movie>> response = new ApiResponse<>();
        response.setData(movieRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createMovie(@RequestBody cinema.dto.request.MovieRequest request) {
        Movie movie = new Movie();
        mapMovieRequest(request, movie);
        return ResponseEntity.ok(movieRepository.save(movie));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable Long id, @RequestBody cinema.dto.request.MovieRequest request) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));
        mapMovieRequest(request, movie);
        return ResponseEntity.ok(movieRepository.save(movie));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        movieRepository.deleteById(id);
        return ResponseEntity.ok("Xóa phim thành công");
    }

    private void mapMovieRequest(cinema.dto.request.MovieRequest request, Movie movie) {
        movie.setTitle(request.getTitle());
        movie.setTitleEn(request.getTitleEn());
        movie.setSlug(request.getSlug());
        movie.setDescription(request.getDescription());
        movie.setDuration(request.getDuration());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setLanguage(request.getLanguage());
        movie.setRated(request.getRated());
        movie.setStatus(request.getStatus());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setBannerUrl(request.getBannerUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setBannerUrl(request.getBannerUrl());
        movie.setDirector(request.getDirector());
        movie.setCastMembers(request.getCastMembers());
        movie.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        if (request.getAverageRating() != null) {
            movie.setAverageRating(request.getAverageRating());
        } else {
            movie.setAverageRating(BigDecimal.ZERO); // Chuẩn mực của BigDecimal
        }
        // ĐOẠN CODE CẦU NỐI: Tự động tách chữ và tìm ID để lưu vào bảng movie_genres
        if (request.getGenre() != null && !request.getGenre().isEmpty()) {
            java.util.Set<cinema.entity.Genre> genreSet = new java.util.HashSet<>();
            // Cắt chuỗi nếu gõ nhiều thể loại (VD: "Hành động, Hài hước")
            String[] genreNames = request.getGenre().split(",");
            for (String gName : genreNames) {
                // Dò tìm trong Database, nếu thấy thì add vào phim
                genreRepository.findByName(gName.trim()).ifPresent(genreSet::add);
            }
            movie.setGenres(genreSet);
        }
    }
}