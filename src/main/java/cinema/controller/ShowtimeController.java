package cinema.controller;

import cinema.dto.response.*;
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
@CrossOrigin("*")
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

    // THÊM API NÀY ĐỂ FRONTEND GỌI LẤY GHẾ
    // THÊM API NÀY ĐỂ FRONTEND GỌI LẤY GHẾ
    @GetMapping("/{id}/booking-data")
    public ResponseEntity<ApiResponse<BookingDataResponse>> getBookingData(@PathVariable Long id) {
        ApiResponse<BookingDataResponse> response = new ApiResponse<>();
        response.setMessage("Lấy dữ liệu phòng chiếu thành công");
        response.setData(showtimeService.getBookingData(id));
        return ResponseEntity.ok(response);
    }
    @GetMapping("/by-cinema")
    public ResponseEntity<ApiResponse<List<MovieShowtimeResponse>>> getShowtimesByCinema(
            @RequestParam Long cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        ApiResponse<List<MovieShowtimeResponse>> response = new ApiResponse<>();
        response.setData(showtimeService.getShowtimesByCinema(cinemaId, date));
        return ResponseEntity.ok(response);
    }

}