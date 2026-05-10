package cinema.service.impl;

import cinema.dto.response.*;
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

        Map<Cinema, List<Showtime>> groupedByCinema = rawShowtimes.stream()
                .collect(Collectors.groupingBy(st -> st.getHall().getCinema()));

        return groupedByCinema.entrySet().stream().map(entry -> {
            Cinema cinema = entry.getKey();
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

    @Override
    public List<MovieShowtimeResponse> getShowtimesByCinema(Long cinemaId, LocalDate date) {
        List<Showtime> rawShowtimes = showtimeRepository.findShowtimesByCinemaAndDate(cinemaId, date);

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

    // HÀM DUY NHẤT LẤY DỮ LIỆU ĐẶT GHẾ (Đã gộp và fix lỗi Type)
    @Override
    public BookingDataResponse getBookingData(Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu với ID: " + showtimeId));

        Movie movie = showtime.getMovie();
        Hall hall = showtime.getHall();
        Cinema cinema = hall.getCinema();

        // 1. Lấy tất cả ghế
        List<Seat> seats = seatRepository.findByHallId(hall.getId());

        // 2. Lấy giá tiền và convert sang Map (Dùng Byte cho Key vì ID của SeatType là kiểu Byte/TINYINT)
        List<ShowtimeSeatPrice> prices = showtimeSeatPriceRepository.findByShowtimeId(showtimeId);
        Map<Byte, BigDecimal> priceMap = prices.stream()
                .collect(Collectors.toMap(p -> p.getSeatType().getId(), ShowtimeSeatPrice::getPrice));

        BigDecimal basePrice = showtime.getBasePrice();

        // 3. Lấy trạng thái ghế (Dùng .name() để lấy String từ Enum)
        List<ShowtimeSeatStatus> statuses = showtimeSeatStatusRepository.findByShowtimeId(showtimeId);
        Map<Long, String> statusMap = statuses.stream()
                .collect(Collectors.toMap(
                        s -> s.getSeat().getId(),
                        s -> s.getStatus().name()
                ));

        // 4. Lắp ráp dữ liệu
        List<SeatDetailDTO> seatDTOs = seats.stream().map(seat -> {
            // Kiểm tra trạng thái
            String currentStatus = statusMap.getOrDefault(seat.getId(), "AVAILABLE");

            // Logic giá thông minh: Lấy giá riêng, nếu không có lấy giá gốc * hệ số
            BigDecimal seatPrice = priceMap.get(seat.getSeatType().getId());
            if (seatPrice == null) {
                seatPrice = basePrice.multiply(seat.getSeatType().getPriceMultiplier());
            }

            return SeatDetailDTO.builder()
                    .seatId(seat.getId())
                    .name(seat.getSeatCode())
                    .rowName(seat.getRowLabel())
                    .colIndex(seat.getColNumber().intValue())
                    .typeId(seat.getSeatType().getId().intValue()) // Ép kiểu Byte sang int cho DTO
                    .status(currentStatus)
                    .price(seatPrice.doubleValue())// Ép kiểu BigDecimal sang Double cho DTO
                    .isActive(seat.getIsActive())
                    .build();
        }).collect(Collectors.toList());

        // 5. Đóng gói JSON
        return BookingDataResponse.builder()
                .movieTitle(movie.getTitle())
                .posterUrl(movie.getPosterUrl())
                .cinemaName(cinema.getName())
                .hallName(hall.getName())
                .showDate(showtime.getStartTime().toLocalDate().toString())
                .startTime(showtime.getStartTime().toLocalTime().toString())
                .seats(seatDTOs)
                .build();
    }
}