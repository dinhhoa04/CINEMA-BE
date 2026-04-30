package cinema.controller;

import cinema.dto.response.ApiResponse;
import cinema.dto.response.CinemaShowtimeResponse;
import cinema.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CinemaShowtimeResponse>>> searchShowtimes(
            @RequestParam Long movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String city,
            @RequestParam String chain) {

        List<CinemaShowtimeResponse> data = showtimeService.getShowtimes(movieId, date, city, chain);

        ApiResponse<List<CinemaShowtimeResponse>> response = new ApiResponse<>();
        response.setMessage("Lấy lịch chiếu thành công");
        response.setData(data);

        return ResponseEntity.ok(response);
    }
}