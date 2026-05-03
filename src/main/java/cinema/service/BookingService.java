package cinema.service;
import cinema.dto.request.BookingRequest;
import cinema.dto.response.BookingHistoryResponse;

import java.util.List;

public interface BookingService {
    String createBooking(BookingRequest request, String email);
    List<BookingHistoryResponse> getUserBookings(String email);
}