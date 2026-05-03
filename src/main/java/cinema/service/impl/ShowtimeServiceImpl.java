package cinema.service.impl;

import cinema.dto.response.BookingPageDataResponse;
import cinema.dto.response.CinemaShowtimeResponse;
import cinema.dto.response.SeatResponse;
import cinema.dto.response.ShowtimeDetailResponse;
import cinema.entity.*;
import cinema.repository.*;
import cinema.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import cinema.dto.response.MovieShowtimeResponse;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeSeatPriceRepository showtimeSeatPriceRepository;
    private final ShowtimeSeatStatusRepository showtimeSeatStatusRepository;

    @Override
    public List<CinemaShowtimeResponse> getShowtimes(Long movieId, LocalDate date, String city, String chainName) {
        List<Showtime> rawShowtimes = showtimeRepository.findShowtimesByFilters(movieId, date, city, chainName);

        Map<cinema.entity.Cinema, List<Showtime>> groupedByCinema = rawShowtimes.stream()
                .collect(Collectors.groupingBy(st -> st.getHall().getCinema()));

        return groupedByCinema.entrySet().stream().map(entry -> {
            cinema.entity.Cinema cinema = entry.getKey();
            List<Showtime> showtimes = entry.getValue();

            List<ShowtimeDetailResponse> timeResponses = showtimes.stream()
                    .map(st -> ShowtimeDetailResponse.builder()
                            .showtimeId(st.getId())
                            .startTime(st.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                            .format(st.getSubtitle() != null ? st.getSubtitle() : "2D")
                            .build())
                    .collect(Collectors.toList());

            return CinemaShowtimeResponse.builder()
                    .cinemaId(cinema.getId())
                    .cinemaName(cinema.getName())
                    .address(cinema.getAddress())
                    .times(timeResponses)
                    .build();

        }).collect(Collectors.toList());
    }

    // THÊM HÀM MỚI ĐỂ LẤY DỮ LIỆU CHO TRANG CHỌN GHẾ
    @Override
    public BookingPageDataResponse getBookingData(Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu!"));
        Hall hall = showtime.getHall();
        Movie movie = showtime.getMovie();

        // 1. Lấy tất cả ghế của phòng chiếu
        List<Seat> seats = seatRepository.findByHallId(hall.getId());

        // 2. Lấy giá tiền CẤU HÌNH RIÊNG (nếu có)
        List<ShowtimeSeatPrice> prices = showtimeSeatPriceRepository.findByShowtimeId(showtimeId);
        Map<Byte, BigDecimal> priceMap = prices.stream()
                .collect(Collectors.toMap(p -> p.getSeatType().getId(), ShowtimeSeatPrice::getPrice));

        // CHUẨN BỊ GIÁ GỐC ĐỂ TÍNH TOÁN NẾU KHÔNG CÓ GIÁ RIÊNG
        BigDecimal basePrice = showtime.getBasePrice();

        // 3. Lấy trạng thái ghế
        List<ShowtimeSeatStatus> statuses = showtimeSeatStatusRepository.findByShowtimeId(showtimeId);
        Map<Long, String> statusMap = statuses.stream()
                .collect(Collectors.toMap(s -> s.getSeat().getId(), s -> s.getStatus().name()));

        // 4. Map dữ liệu gộp lại
        List<SeatResponse> seatResponses = seats.stream().map(seat -> {
            // LOGIC TÍNH GIÁ THÔNG MINH:
            BigDecimal seatPrice = priceMap.get(seat.getSeatType().getId());
            if (seatPrice == null) {
                // Nếu chưa cấu hình giá -> Lấy Giá gốc suất chiếu * Hệ số loại ghế
                seatPrice = basePrice.multiply(seat.getSeatType().getPriceMultiplier());
            }

            return SeatResponse.builder()
                    .id(seat.getId())
                    .name(seat.getSeatCode())
                    .row(seat.getRowLabel())
                    .col(seat.getColNumber())
                    .type(seat.getSeatType().getName())
                    .price(seatPrice) // <-- Dùng giá đã tính toán
                    .status(statusMap.getOrDefault(seat.getId(), "AVAILABLE"))
                    .build();
        }).collect(Collectors.toList());

        return BookingPageDataResponse.builder()
                .showtimeId(showtime.getId())
                .movieTitle(movie.getTitle())
                .posterUrl(movie.getPosterUrl())
                .cinemaName(hall.getCinema().getName())
                .hallName(hall.getName())
                .showDate(showtime.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .showTime(showtime.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .format(showtime.getSubtitle() != null ? showtime.getSubtitle() : "2D")
                .seats(seatResponses)
                .build();
    }
    @Override
    public List<MovieShowtimeResponse> getShowtimesByCinema(Long cinemaId, LocalDate date) {
        List<Showtime> rawShowtimes = showtimeRepository.findShowtimesByCinemaAndDate(cinemaId, date);

        // Gom nhóm theo Phim
        Map<Movie, List<Showtime>> groupedByMovie = rawShowtimes.stream()
                .collect(Collectors.groupingBy(Showtime::getMovie));

        return groupedByMovie.entrySet().stream().map(entry -> {
            Movie movie = entry.getKey();
            List<Showtime> showtimes = entry.getValue();

            List<ShowtimeDetailResponse> timeResponses = showtimes.stream()
                    .map(st -> ShowtimeDetailResponse.builder()
                            .showtimeId(st.getId())
                            .startTime(st.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                            .format(st.getSubtitle() != null ? st.getSubtitle() : "2D")
                            .build())
                    .collect(Collectors.toList());

            return MovieShowtimeResponse.builder()
                    .movieId(movie.getId())
                    .movieTitle(movie.getTitle())
                    .posterUrl(movie.getPosterUrl())
                    .rated(movie.getRated() != null ? movie.getRated().name() : "T18")
                    .times(timeResponses)
                    .build();
        }).collect(Collectors.toList());
    }
}