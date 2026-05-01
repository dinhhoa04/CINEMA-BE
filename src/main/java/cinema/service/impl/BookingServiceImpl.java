package cinema.service.impl;

import cinema.dto.request.BookingRequest;
import cinema.entity.*;
import cinema.enums.BookingStatus;
import cinema.enums.SeatStatus;
import cinema.repository.*;
import cinema.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final FoodItemRepository foodItemRepository;
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final BookingFoodItemRepository bookingFoodItemRepository;
    private final ShowtimeSeatStatusRepository showtimeSeatStatusRepository;

    @Override
    @Transactional // Đảm bảo nếu lỗi ở bước nào thì Rollback lại toàn bộ
    public String createBooking(BookingRequest request, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId()).orElseThrow();

        // 1. Tạo đơn hàng (Booking)
        Booking booking = new Booking();
        // Tự tạo mã vé ngẫu nhiên 8 ký tự (VD: CB-8A7B9C)
        booking.setBookingCode("CB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setSubtotal(request.getFinalTotal()); // (Thực tế nên tự tính lại ở BE để bảo mật)
        booking.setTotalAmount(request.getFinalTotal());
        booking.setFinalAmount(request.getFinalTotal());
        booking.setStatus(BookingStatus.PAID); // Gắn cờ ĐÃ THANH TOÁN ẢO
        bookingRepository.save(booking);

        // 2. Lưu chi tiết Ghế & Cập nhật trạng thái ghế thành BOOKED
        for (Long seatId : request.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId).orElseThrow();

            BookingSeat bs = new BookingSeat();
            bs.setBooking(booking);
            bs.setSeat(seat);
            bs.setSeatCode(seat.getSeatCode());
            bs.setSeatTypeName(seat.getSeatType().getName());
            bs.setPrice(BigDecimal.ZERO); // Tạm lưu 0 để nhanh, thực tế query giá
            bookingSeatRepository.save(bs);

            // Khóa ghế lại
            ShowtimeSeatStatus status = showtimeSeatStatusRepository.findByShowtimeIdAndSeatId(showtime.getId(), seatId)
                    .orElse(new ShowtimeSeatStatus());
            status.setShowtime(showtime);
            status.setSeat(seat);
            status.setStatus(SeatStatus.BOOKED);
            status.setBooking(booking);
            showtimeSeatStatusRepository.save(status);
        }

        // 3. Cập nhật số lượng ghế đã bán của suất chiếu
        showtime.setBookedSeats((short) (showtime.getBookedSeats() + request.getSeatIds().size()));
        showtimeRepository.save(showtime);

        // 4. Lưu chi tiết Đồ ăn (nếu có mua)
        if (request.getCart() != null && !request.getCart().isEmpty()) {
            for (Map.Entry<Long, Integer> entry : request.getCart().entrySet()) {
                FoodItem food = foodItemRepository.findById(entry.getKey()).orElseThrow();
                BookingFoodItem bf = new BookingFoodItem();
                bf.setBooking(booking);
                bf.setFoodItem(food);
                bf.setQuantity(entry.getValue().byteValue());
                bf.setUnitPrice(food.getPrice());
                bf.setSubtotal(food.getPrice().multiply(new BigDecimal(entry.getValue())));
                bookingFoodItemRepository.save(bf);
            }
        }

        // Trả về mã vé để Frontend hiển thị
        return booking.getBookingCode();
    }
}