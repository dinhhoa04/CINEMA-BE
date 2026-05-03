package cinema.service;

import cinema.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface ShowtimeService {
    // Khai báo hàm lấy lịch chiếu với 4 tham số lọc
    List<CinemaShowtimeResponse> getShowtimes(Long movieId, LocalDate date, String city, String chainName);
    BookingDataResponse getBookingData(Long showtimeId);

    // Trong ShowtimeServiceImpl.java
    List<MovieShowtimeResponse> getShowtimesByCinema(Long cinemaId, LocalDate date);
}