package cinema.controller;

import cinema.dto.response.ApiResponse; // Đảm bảo bạn có file ApiResponse ở folder này
import cinema.entity.Booking;           // Đảm bảo bạn có file Booking ở folder này
import cinema.enums.BookingStatus;      // Đảm bảo bạn có file BookingStatus
import cinema.exception.AppException;   // Đảm bảo bạn có file AppException
import cinema.repository.BookingRepository;
import cinema.service.MomoService;      // Đã sửa theo bước trước
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap; // Thêm dòng này để dùng Map

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final MomoService momoService;
    private final BookingRepository bookingRepository;

    // ── FE gọi endpoint này để lấy link MoMo ─────────────────
    @PostMapping("/momo/create")
    public ResponseEntity<ApiResponse<String>> createMomoPayment(
            @RequestParam String bookingCode) {

        // 1. Tìm booking
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new AppException(404, "Không tìm thấy booking"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new AppException(400, "Booking này đã được xử lý");
        }

        try {
            // 2. Gọi MoMo tạo link
            long amount = booking.getFinalAmount().longValue();
            String payUrl = momoService.createPaymentUrl(bookingCode, amount);

            // 3. Trả về payUrl cho FE
            ApiResponse<String> response = new ApiResponse<>();
            response.setMessage("Tạo link thanh toán thành công");
            response.setData(payUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            throw new AppException(500, "Lỗi tạo thanh toán MoMo: " + e.getMessage());
        }
    }

    // ── MoMo tự gọi endpoint này sau khi thanh toán xong ─────
    // (Đây là IPN — cái quan trọng nhất)
    @PostMapping("/momo/ipn")
    public ResponseEntity<String> handleMomoIpn(
            @RequestBody Map<String, String> params) {

        // 1. Xác minh chữ ký — rất quan trọng!
        if (!momoService.verifyIpnSignature(params)) {
            return ResponseEntity.badRequest().body("INVALID SIGNATURE");
        }

        // 2. Kiểm tra kết quả thanh toán
        String resultCode = params.get("resultCode");
        String orderId    = params.get("orderId"); // format: bookingCode_timestamp

        // orderId có dạng: "CGV123_1716123456789" → lấy phần bookingCode
        String bookingCode = orderId.contains("_")
                ? orderId.substring(0, orderId.lastIndexOf("_"))
                : orderId;

        bookingRepository.findByBookingCode(bookingCode).ifPresent(booking -> {
            if ("0".equals(resultCode)) {
                // ✅ Thanh toán thành công
                booking.setStatus(BookingStatus.PAID);
            } else {
                // ❌ Thanh toán thất bại / huỷ
                booking.setStatus(BookingStatus.CANCELLED);
            }
            bookingRepository.save(booking);
        });

        // MoMo yêu cầu trả về "0" nghĩa là BE đã nhận và xử lý OK
        return ResponseEntity.ok("0");
    }

    // ── FE gọi để kiểm tra trạng thái booking sau khi redirect về ─
    @GetMapping("/status/{bookingCode}")
    public ResponseEntity<ApiResponse<String>> checkStatus(
            @PathVariable String bookingCode) {

        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new AppException(404, "Không tìm thấy booking"));

        ApiResponse<String> response = new ApiResponse<>();
        response.setData(booking.getStatus().name());
        response.setMessage("Lấy trạng thái thành công");
        return ResponseEntity.ok(response);
    }
}