package cinema.controller;

import cinema.dto.request.CinemaRequest;
import cinema.dto.request.HallRequest;
import cinema.dto.response.ApiResponse;
import cinema.entity.*;
import cinema.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SystemController {

    private final CityRepository cityRepository;
    private final CinemaChainRepository cinemaChainRepository;
    private final CinemaRepository cinemaRepository;
    private final HallRepository hallRepository;
    private final HallTypeRepository hallTypeRepository;

    // --- CÁC API CŨ CỦA BẠN ---
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
        response.setData(cinemaRepository.findByCinemaChainId(chainId)); // Đảm bảo repo bạn có hàm này
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cinemas/{id}")
    public ResponseEntity<ApiResponse<Cinema>> getCinemaById(@PathVariable Long id) {
        Cinema cinema = cinemaRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy rạp"));
        ApiResponse<Cinema> response = new ApiResponse<>();
        response.setData(cinema);
        return ResponseEntity.ok(response);
    }

    // --- BỔ SUNG API CHO RẠP (CINEMA ADMIN) ---
    @GetMapping("/cinemas/all")
    public ResponseEntity<ApiResponse<List<Cinema>>> getAllCinemas() {
        ApiResponse<List<Cinema>> response = new ApiResponse<>();
        response.setData(cinemaRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cinemas")
    public ResponseEntity<?> createCinema(@RequestBody CinemaRequest request) {
        Cinema cinema = new Cinema();
        mapCinemaRequest(request, cinema);
        return ResponseEntity.ok(cinemaRepository.save(cinema));
    }

    @PutMapping("/cinemas/{id}")
    public ResponseEntity<?> updateCinema(@PathVariable Long id, @RequestBody CinemaRequest request) {
        Cinema cinema = cinemaRepository.findById(id).orElseThrow(() -> new RuntimeException("Lỗi"));
        mapCinemaRequest(request, cinema);
        return ResponseEntity.ok(cinemaRepository.save(cinema));
    }

    @DeleteMapping("/cinemas/{id}")
    public ResponseEntity<?> deleteCinema(@PathVariable Long id) {
        cinemaRepository.deleteById(id);
        return ResponseEntity.ok("Xóa Rạp thành công");
    }

    // --- BỔ SUNG API CHO PHÒNG CHIẾU (HALL ADMIN) ---
    @GetMapping("/hall-types")
    public ResponseEntity<ApiResponse<List<HallType>>> getHallTypes() {
        ApiResponse<List<HallType>> response = new ApiResponse<>();
        response.setData(hallTypeRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/halls")
    public ResponseEntity<ApiResponse<List<Hall>>> getAllHalls() {
        ApiResponse<List<Hall>> response = new ApiResponse<>();
        response.setData(hallRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/halls")
    public ResponseEntity<?> createHall(@RequestBody HallRequest request) {
        Hall hall = new Hall();
        mapHallRequest(request, hall);
        return ResponseEntity.ok(hallRepository.save(hall));
    }

    @PutMapping("/halls/{id}")
    public ResponseEntity<?> updateHall(@PathVariable Long id, @RequestBody HallRequest request) {
        Hall hall = hallRepository.findById(id).orElseThrow(() -> new RuntimeException("Lỗi"));
        mapHallRequest(request, hall);
        return ResponseEntity.ok(hallRepository.save(hall));
    }

    @DeleteMapping("/halls/{id}")
    public ResponseEntity<?> deleteHall(@PathVariable Long id) {
        hallRepository.deleteById(id);
        return ResponseEntity.ok("Xóa Phòng thành công");
    }

    // --- HÀM MAPPER PHỤ TRỢ ---
    private void mapCinemaRequest(CinemaRequest request, Cinema cinema) {
        cinema.setName(request.getName());
        cinema.setSlug(request.getSlug());
        cinema.setAddress(request.getAddress());
        cinema.setPhone(request.getPhone());
        cinema.setEmail(request.getEmail());
        cinema.setLogoUrl(request.getLogoUrl());
        cinema.setIsActive(request.getIsActive());

        if (request.getCityId() != null) cinema.setCity(cityRepository.findById(request.getCityId()).orElse(null));
        if (request.getChainId() != null) cinema.setCinemaChain(cinemaChainRepository.findById(request.getChainId()).orElse(null));
    }

    private void mapHallRequest(HallRequest request, Hall hall) {
        hall.setName(request.getName());
        hall.setTotalRows(request.getTotalRows());
        hall.setTotalCols(request.getTotalCols());
        hall.setIsActive(request.getIsActive());
        // Tự động tính tổng số ghế: Hàng x Cột
        hall.setTotalSeats((short) (request.getTotalRows() * request.getTotalCols()));

        if (request.getCinemaId() != null) hall.setCinema(cinemaRepository.findById(request.getCinemaId()).orElse(null));
        if (request.getHallTypeId() != null) hall.setHallType(hallTypeRepository.findById(request.getHallTypeId()).orElse(null));
    }
}