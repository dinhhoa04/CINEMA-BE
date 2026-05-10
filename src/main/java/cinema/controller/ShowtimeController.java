package cinema.controller;

import cinema.dto.request.ShowtimeRequest;
import cinema.dto.response.*;
import cinema.entity.Hall;
import cinema.entity.Showtime;
import cinema.repository.HallRepository;
import cinema.repository.MovieRepository;
import cinema.repository.ShowtimeRepository;
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

    // Bổ sung 3 khai báo Repository này để Spring Boot tiêm (inject) dữ liệu vào và hết báo lỗi đỏ
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;

    // ==========================================
    // CÁC API DÀNH CHO KHÁCH HÀNG (GIỮ NGUYÊN)
    // ==========================================
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

    // ==========================================
    // CÁC API DÀNH CHO ADMIN QUẢN LÝ (THÊM MỚI)
    // ==========================================
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Showtime>>> getAllShowtimesAdmin() {
        ApiResponse<List<Showtime>> response = new ApiResponse<>();
        response.setData(showtimeRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createShowtime(@RequestBody ShowtimeRequest request) {
        Showtime showtime = new Showtime();
        mapShowtimeRequest(request, showtime);
        return ResponseEntity.ok(showtimeRepository.save(showtime));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateShowtime(@PathVariable Long id, @RequestBody ShowtimeRequest request) {
        Showtime showtime = showtimeRepository.findById(id).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy lịch chiếu"));
        mapShowtimeRequest(request, showtime);
        return ResponseEntity.ok(showtimeRepository.save(showtime));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShowtime(@PathVariable Long id) {
        showtimeRepository.deleteById(id);
        return ResponseEntity.ok("Xóa lịch chiếu thành công");
    }

    // Hàm phụ trợ dùng chung cho Thêm/Sửa lịch chiếu
    private void mapShowtimeRequest(ShowtimeRequest request, Showtime showtime) {
        showtime.setMovie(movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim")));

        Hall hall = hallRepository.findById(request.getHallId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));

        showtime.setHall(hall);
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(request.getEndTime());
        showtime.setLanguage(request.getLanguage());
        showtime.setBasePrice(request.getBasePrice());
        showtime.setSubtitle(request.getSubtitle());
        showtime.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        showtime.setTotalSeats(hall.getTotalSeats()); // Tự động lấy số ghế từ phòng
    }
}