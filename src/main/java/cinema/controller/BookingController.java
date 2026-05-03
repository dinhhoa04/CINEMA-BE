package cinema.controller;

import cinema.dto.request.BookingRequest;
import cinema.dto.response.ApiResponse;
import cinema.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import cinema.dto.response.BookingHistoryResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createBooking(@RequestBody BookingRequest request, Principal principal) {
        // principal.getName() chính là Email của user đang đăng nhập (nhờ JwtAuthenticationFilter)
        String bookingCode = bookingService.createBooking(request, principal.getName());

        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Đặt vé thành công!");
        response.setData(bookingCode);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<BookingHistoryResponse>>> getBookingHistory(Principal principal) {
        // Hàm này tự lấy Email từ cái Token mà Frontend gửi lên
        List<BookingHistoryResponse> data = bookingService.getUserBookings(principal.getName());

        ApiResponse<List<BookingHistoryResponse>> response = new ApiResponse<>();
        response.setMessage("Lấy lịch sử đặt vé thành công!");
        response.setData(data);

        return ResponseEntity.ok(response);
    }
}