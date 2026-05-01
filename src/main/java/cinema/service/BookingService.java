package cinema.service;
import cinema.dto.request.BookingRequest;

public interface BookingService {
    String createBooking(BookingRequest request, String email);
}