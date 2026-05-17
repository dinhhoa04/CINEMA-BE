package cinema.service.impl;

import cinema.dto.request.BookingRequest;
import cinema.dto.response.BookingAdminResponse;
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
import cinema.dto.response.BookingHistoryResponse;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
    private final PromotionServiceImpl promotionService;


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

        if (request.getPromoCode() != null && !request.getPromoCode().trim().isEmpty()) {
            // Lấy ID user hiện tại (nếu hàm createBooking của bạn chưa truyền userId vào thì tạm hardcode 1L giống lúc test)
            Long currentUserId = booking.getUser().getId();

            // Gọi sang hàm commit bên PromotionService để: Tăng usageCount và Ghi vào bảng user_promotions
            promotionService.commitPromotionUsage(request.getPromoCode(), currentUserId);
        }
        // Trả về mã vé để Frontend hiển thị
        return booking.getBookingCode();
    }
    @Override
    public List<BookingHistoryResponse> getUserBookings(String email) {
        // 1. Tìm user theo Email (lấy từ JWT token)
        User user = userRepository.findByEmail(email).orElseThrow();

        // 2. Kéo toàn bộ vé của user này lên
        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        // 3. Map sang DTO để trả về Frontend
        return bookings.stream().map(b -> {
            Showtime st = b.getShowtime();
            Movie m = st.getMovie();

            // Lấy danh sách tên ghế (VD: gom thành chuỗi "F5, F6")
            List<BookingSeat> seats = bookingSeatRepository.findByBookingId(b.getId());
            String seatNames = seats.stream()
                    .map(BookingSeat::getSeatCode)
                    .collect(Collectors.joining(", "));

            return BookingHistoryResponse.builder()
                    .bookingCode(b.getBookingCode())
                    .movieTitle(m.getTitle())
                    .posterUrl(m.getPosterUrl())
                    .cinemaName(st.getHall().getCinema().getName())
                    .hallName(st.getHall().getName())
                    .showDate(st.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .showTime(st.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                    .seatNames(seatNames)
                    .totalAmount(b.getFinalAmount())
                    .status(b.getStatus().name())
                    .bookingDate(b.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<BookingAdminResponse> getAllBookingsForAdmin() {
        // Lấy tất cả vé, xếp mới nhất lên đầu
        List<Booking> bookings = bookingRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));

        return bookings.stream().map(b -> {
            Showtime st = b.getShowtime();
            User user = b.getUser();

            // Lấy ghế (Nếu sau này bị chậm, ta sẽ tối ưu lại bằng IN query)
            List<BookingSeat> seats = bookingSeatRepository.findByBookingId(b.getId());
            String seatNames = seats.stream().map(BookingSeat::getSeatCode).collect(Collectors.joining(", "));

            return BookingAdminResponse.builder()
                    .id(b.getId())
                    .bookingCode(b.getBookingCode())
                    .customerName(user.getFullName())
                    .customerPhone(user.getPhone())
                    .customerEmail(user.getEmail())
                    .movieTitle(st.getMovie().getTitle())
                    .cinemaName(st.getHall().getCinema().getName())
                    .hallName(st.getHall().getName())
                    .showTime(st.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                    .seatNames(seatNames)
                    .finalAmount(b.getFinalAmount())
                    .status(b.getStatus().name())
                    .paymentMethod("VNPAY") // Tạm fix cứng hoặc lấy từ Payment nếu bạn đã code bảng Payment
                    .bookingTime(b.getCreatedAt())
                    .qrCodeUrl(b.getQrCodeUrl()) // Lấy QR
                    .build();
        }).collect(Collectors.toList());
    }


    @Override
    public void checkInTicket(String bookingCode) {
        // Tìm vé theo mã vé (bookingCode)
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã vé: " + bookingCode));

        // Kiểm tra xem vé có đúng là Đang chờ soát (PAID) không
        if (!booking.getStatus().name().equals("PAID")) {
            throw new RuntimeException("Vé này đã được soát hoặc đã bị hủy, không thể Check-in!");
        }

        // Đổi trạng thái sang CHECKED_IN (hoặc dùng Enum BookingStatus.CHECKED_IN tùy cấu trúc của bạn)
        booking.setStatus(cinema.enums.BookingStatus.valueOf("CHECKED_IN"));

        // Lưu xuống Database
        bookingRepository.save(booking);
    }
}