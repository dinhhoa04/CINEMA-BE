package cinema.controller;

import cinema.dto.request.SeatItemRequest;
import cinema.entity.Hall;
import cinema.entity.Seat;
import cinema.entity.SeatType;
import cinema.repository.HallRepository;
import cinema.repository.SeatRepository;
import cinema.repository.SeatTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SeatController {

    private final SeatRepository seatRepository;
    private final SeatTypeRepository seatTypeRepository;
    private final HallRepository hallRepository;

    // 1. Lấy danh sách các Loại Ghế (Để vẽ bảng màu tô cho Admin)
    @GetMapping("/types")
    public ResponseEntity<List<SeatType>> getSeatTypes() {
        return ResponseEntity.ok(seatTypeRepository.findAll());
    }

    // 2. Lấy sơ đồ ghế hiện tại của một phòng chiếu
    @GetMapping("/hall/{hallId}")
    public ResponseEntity<List<Seat>> getSeatsByHall(@PathVariable Long hallId) {
        return ResponseEntity.ok(seatRepository.findByHallId(hallId));
    }

    // 3. Lưu toàn bộ Sơ đồ ghế (Bulk Insert/Update)
    @PostMapping("/hall/{hallId}/bulk")
    @Transactional // Đảm bảo lỗi ở 1 ghế thì rollback toàn bộ, không bị lưu thiếu
    public ResponseEntity<?> saveSeatMap(@PathVariable Long hallId, @RequestBody List<SeatItemRequest> seatRequests) {
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Phòng chiếu"));

        List<Seat> existingSeats = seatRepository.findByHallId(hallId);

        for (SeatItemRequest req : seatRequests) {
            // Tìm xem ghế ở toạ độ (Hàng, Cột) này đã có trong DB chưa
            Seat seat = existingSeats.stream()
                    .filter(s -> s.getRowLabel().equals(req.getRowLabel()) && s.getColNumber().equals(req.getColNumber()))
                    .findFirst()
                    .orElse(new Seat()); // Nếu chưa có thì tạo ghế mới

            SeatType type = seatTypeRepository.findById(req.getSeatTypeId())
                    .orElseThrow(() -> new RuntimeException("Loại ghế không hợp lệ"));

            seat.setHall(hall);
            seat.setSeatType(type);
            seat.setRowLabel(req.getRowLabel());
            seat.setColNumber(req.getColNumber());
            seat.setSeatCode(req.getRowLabel() + req.getColNumber()); // Tạo mã VD: A1, B5
            seat.setIsActive(req.getIsActive());

            seatRepository.save(seat);
        }

        return ResponseEntity.ok("Lưu sơ đồ ghế thành công!");
    }
}