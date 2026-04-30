package cinema.controller;

import cinema.dto.response.ApiResponse;
import cinema.entity.CinemaChain;
import cinema.entity.City;
import cinema.repository.CinemaChainRepository;
import cinema.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class SystemController {

    private final CityRepository cityRepository;
    private final CinemaChainRepository cinemaChainRepository;

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
}