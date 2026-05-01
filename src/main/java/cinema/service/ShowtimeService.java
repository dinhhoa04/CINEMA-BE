package cinema.service;

import cinema.dto.response.BookingPageDataResponse;
import cinema.dto.response.CinemaShowtimeResponse;
import java.time.LocalDate;
import java.util.List;

public interface ShowtimeService {
    // Khai báo hàm lấy lịch chiếu với 4 tham số lọc
    List<CinemaShowtimeResponse> getShowtimes(Long movieId, LocalDate date, String city, String chainName);
    BookingPageDataResponse getBookingData(Long showtimeId);
}