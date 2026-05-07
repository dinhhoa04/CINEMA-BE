package cinema.controller;

import cinema.dto.request.PromotionApplyRequest;
import cinema.dto.request.PromotionRequest;
import cinema.dto.response.PromotionResponse;
import cinema.entity.Promotion;
import cinema.service.impl.PromotionServiceImpl;
import cinema.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PromotionController {

    private final PromotionServiceImpl promotionService;
    private final PromotionRepository promotionRepository;

    // 1. Áp dụng mã (Dành cho Frontend khách hàng)
    @PostMapping("/apply")
    public ResponseEntity<?> applyPromotion(@RequestBody PromotionApplyRequest request) {
        try {
            Long currentUserId = 1L; // Mock User
            PromotionResponse response = promotionService.applyCode(request, currentUserId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Lấy tất cả (Dành cho Admin - Lấy cả cái đã tắt)
    @GetMapping
    public ResponseEntity<?> getAllPromotions() {
        return ResponseEntity.ok(promotionRepository.findAll());
    }

    // 3. Thêm mới Khuyến mãi
    @PostMapping
    public ResponseEntity<?> createPromotion(@RequestBody PromotionRequest request) {
        Promotion promotion = new Promotion();
        mapRequestToEntity(request, promotion);
        promotion.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(promotionRepository.save(promotion));
    }

    // 4. Sửa Khuyến mãi
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePromotion(@PathVariable Long id, @RequestBody PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã khuyến mãi"));
        mapRequestToEntity(request, promotion);
        return ResponseEntity.ok(promotionRepository.save(promotion));
    }

    // 5. Xóa Khuyến mãi
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePromotion(@PathVariable Long id) {
        promotionRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa thành công");
    }

    // Hàm phụ trợ map dữ liệu từ DTO sang Entity
    private void mapRequestToEntity(PromotionRequest request, Promotion promotion) {
        promotion.setCode(request.getCode());
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        // Giả sử Entity của bạn đang dùng Enum hoặc String cho discountType
        // Nếu Entity dùng Enum, hãy đảm bảo Entity có phương thức set tương ứng (ví dụ: setDiscountType(DiscountType.valueOf(request.getDiscountType())))
        // Ở đây tạm thời map trực tiếp, bạn tùy chỉnh nếu Entity của bạn là Enum
        promotion.setDiscountType(cinema.enums.DiscountType.valueOf(request.getDiscountType()));
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setMinOrderAmount(request.getMinOrderAmount());
        promotion.setMaxDiscountAmount(request.getMaxDiscountAmount());
        promotion.setUsageLimit(request.getUsageLimit());
        promotion.setPerUserLimit(request.getPerUserLimit());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setIsActive(request.getIsActive());
        promotion.setImageUrl(request.getImageUrl());
    }
    @GetMapping("/active")
    public ResponseEntity<?> getActivePromotions() {
        return ResponseEntity.ok(promotionRepository.findByIsActiveTrue());
    }
}