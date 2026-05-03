package cinema.repository;

import cinema.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    // Lấy danh sách ghế của 1 vé cụ thể
    List<BookingSeat> findByBookingId(Long bookingId);
}