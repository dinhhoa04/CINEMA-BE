package cinema.service;

import cinema.dto.response.BookingPageDataResponse;
import cinema.dto.response.CinemaShowtimeResponse;
import cinema.dto.response.MovieShowtimeResponse;
import cinema.dto.response.ShowtimeDetailResponse;
import cinema.entity.Movie;
import cinema.entity.Showtime;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ShowtimeService {
    // Khai báo hàm lấy lịch chiếu với 4 tham số lọc
    List<CinemaShowtimeResponse> getShowtimes(Long movieId, LocalDate date, String city, String chainName);
    BookingPageDataResponse getBookingData(Long showtimeId);

    // Trong ShowtimeServiceImpl.java
    List<MovieShowtimeResponse> getShowtimesByCinema(Long cinemaId, LocalDate date);
}