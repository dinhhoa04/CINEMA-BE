package cinema.controller;

import cinema.dto.response.ApiResponse;
import cinema.dto.response.MovieResponse;
import cinema.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // API: Lấy danh sách phim đang chiếu
    @GetMapping("/now-showing")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getNowShowing() {
        ApiResponse<List<MovieResponse>> response = new ApiResponse<>();
        response.setMessage("Lấy danh sách phim thành công");
        response.setData(movieService.getNowShowingMovies());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieById(@PathVariable Long id) {
        // Tạo hộp chứa chuẩn như hàm getNowShowing
        ApiResponse<MovieResponse> response = new ApiResponse<>();
        response.setMessage("Lấy chi tiết phim thành công");
        response.setData(movieService.getMovieById(id));

        return ResponseEntity.ok(response);
    }
    @GetMapping("/coming-soon")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getComingSoon() {
        ApiResponse<List<MovieResponse>> response = new ApiResponse<>();
        response.setData(movieService.getComingSoonMovies());
        return ResponseEntity.ok(response);
    }
}