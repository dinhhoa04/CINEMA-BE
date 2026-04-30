package cinema.service.impl;

import cinema.dto.response.CinemaShowtimeResponse;
import cinema.dto.response.ShowtimeDetailResponse;
import cinema.entity.Showtime;
import cinema.repository.ShowtimeRepository;
import cinema.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;

    @Override
    public List<CinemaShowtimeResponse> getShowtimes(Long movieId, LocalDate date, String city, String chainName) {

        // 1. Lấy tất cả suất chiếu
        List<Showtime> rawShowtimes = showtimeRepository.findShowtimesByFilters(movieId, date, city, chainName);

        // 2. Gom nhóm theo Rạp.
        // FIX LỖI ĐỎ Ở ĐÂY: Đi từ Showtime -> Hall -> Cinema
        Map<cinema.entity.Cinema, List<Showtime>> groupedByCinema = rawShowtimes.stream()
                .collect(Collectors.groupingBy(st -> st.getHall().getCinema()));

        // 3. Xử lý Map và chuyển đổi sang DTO
        return groupedByCinema.entrySet().stream().map(entry -> {
            cinema.entity.Cinema cinema = entry.getKey();
            List<Showtime> showtimes = entry.getValue();

            List<ShowtimeDetailResponse> timeResponses = showtimes.stream()
                    .map(st -> ShowtimeDetailResponse.builder()
                            .showtimeId(st.getId())
                            .startTime(st.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                            // FIX LỖI ĐỎ Ở ĐÂY: Tạm thời set cứng định dạng.
                            // Nếu bảng Hall của anh có loại rạp (ví dụ IMAX), anh có thể đổi thành st.getHall().getHallType().getName() sau.
                            // Lấy trực tiếp dữ liệu từ Database của anh lên giao diện
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
}