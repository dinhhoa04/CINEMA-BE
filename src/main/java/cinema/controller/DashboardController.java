package cinema.controller;

import cinema.dto.response.ApiResponse;
import cinema.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview() {
        Map<String, Object> data = dashboardService.getDashboardOverview();

        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        response.setMessage("Lấy dữ liệu Dashboard thành công");
        response.setData(data);

        return ResponseEntity.ok(response);
    }
}