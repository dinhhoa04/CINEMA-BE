package cinema.controller;

import cinema.dto.response.ApiResponse;
import cinema.dto.response.MovieResponse;
import cinema.entity.Movie;
import cinema.repository.MovieRepository;
import cinema.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MovieController {

    private final MovieService movieService;
    private final MovieRepository movieRepository;

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
}