package cinema.controller;

import cinema.dto.response.ApiResponse;
import cinema.entity.CinemaChain;
import cinema.entity.City;
import cinema.repository.CinemaChainRepository;
import cinema.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cinema.entity.Cinema;
import cinema.repository.CinemaRepository;
import java.util.List;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class SystemController {

    private final CityRepository cityRepository;
    private final CinemaChainRepository cinemaChainRepository;
    private final CinemaRepository cinemaRepository;
    @GetMapping("/cities")
    public ResponseEntity<ApiResponse<List<City>>> getAllCities() {
        ApiResponse<List<City>> response = new ApiResponse<>();
        response.setData(cityRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cinema-chains")
    public ResponseEntity<ApiResponse<List<CinemaChain>>> getAllCinemaChains() {
        ApiResponse<List<CinemaChain>> response = new ApiResponse<>();
        response.setData(cinemaChainRepository.findAll());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/cinemas")
    public ResponseEntity<ApiResponse<List<Cinema>>> getCinemasByChain(@RequestParam Long chainId) {
        ApiResponse<List<Cinema>> response = new ApiResponse<>();
        response.setData(cinemaRepository.findByCinemaChainId(chainId));
        return ResponseEntity.ok(response);
    }
    @GetMapping("/cinemas/{id}")
    public ResponseEntity<ApiResponse<Cinema>> getCinemaById(@PathVariable Long id) {
        // Tìm rạp theo ID, nếu không thấy thì quăng lỗi để khỏi bị crash ngầm
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy rạp chiếu với ID này"));

        ApiResponse<Cinema> response = new ApiResponse<>();
        response.setMessage("Lấy thông tin rạp thành công");
        response.setData(cinema);

        return ResponseEntity.ok(response);
    }
}