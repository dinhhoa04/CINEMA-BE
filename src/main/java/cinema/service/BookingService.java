package cinema.service;
import cinema.dto.request.BookingRequest;
import cinema.dto.response.BookingAdminResponse;
import cinema.dto.response.BookingHistoryResponse;

import java.util.List;

public interface BookingService {
    String createBooking(BookingRequest request, String email);
    List<BookingHistoryResponse> getUserBookings(String email);
    List<BookingAdminResponse> getAllBookingsForAdmin();
    // Thêm dòng này vào interface
    void checkInTicket(String bookingCode);
}