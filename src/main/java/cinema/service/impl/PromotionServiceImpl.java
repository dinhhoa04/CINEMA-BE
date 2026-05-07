package cinema.service.impl;

import cinema.dto.request.PromotionApplyRequest;
import cinema.dto.response.PromotionResponse;
import cinema.entity.Promotion;
import cinema.entity.Showtime;
import cinema.entity.UserPromotion;
import cinema.entity.User;
import cinema.repository.PromotionRepository;
import cinema.repository.ShowtimeRepository;
import cinema.repository.UserPromotionRepository;
import cinema.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl { // Nhớ implements Interface nếu project của bạn có xài

    private final PromotionRepository promotionRepository;
    private final UserPromotionRepository userPromotionRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserRepository userRepository;

    // --- HÀM 1: CHỈ TÍNH TOÁN ĐỂ HIỂN THỊ TIỀN (Chưa trừ số lượng) ---
    public PromotionResponse applyCode(PromotionApplyRequest request, Long userId) {
        Promotion promo = promotionRepository.findByCode(request.getCode())
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại!"));

        if (!promo.getIsActive()) throw new RuntimeException("Mã giảm giá này đã bị vô hiệu hóa!");

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(promo.getStartDate()) || now.isAfter(promo.getEndDate())) {
            throw new RuntimeException("Mã giảm giá chưa tới hạn hoặc đã hết hạn!");
        }

        if (promo.getUsageLimit() != null && promo.getUsageCount() >= promo.getUsageLimit()) {
            throw new RuntimeException("Rất tiếc! Mã giảm giá đã hết lượt sử dụng.");
        }

        // Kiểm tra UserLimit từ file UserPromotion của bạn
        Optional<UserPromotion> userPromoOpt = userPromotionRepository.findByUserIdAndPromotionId(userId, promo.getId());
        if (userPromoOpt.isPresent()) {
            if (userPromoOpt.get().getUsedCount() >= promo.getPerUserLimit()) {
                throw new RuntimeException("Bạn đã hết lượt sử dụng mã giảm giá này!");
            }
        }

        if (promo.getMinOrderAmount() != null && request.getOrderTotal().compareTo(promo.getMinOrderAmount()) < 0) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu " + promo.getMinOrderAmount() + "đ!");
        }

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Suất chiếu không tồn tại!"));

        String validIds = promo.getApplicableCinemaIds(); // Cột nãy mình mới thêm
        if (validIds != null && !validIds.trim().isEmpty()) {
            List<String> allowedCinemas = Arrays.asList(validIds.split(","));
            if (!allowedCinemas.contains(String.valueOf(showtime.getHall().getCinema().getId()))) {
                throw new RuntimeException("Mã này không áp dụng cho rạp phim bạn đang chọn!");
            }
        }

        // TÍNH TIỀN
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (promo.getDiscountType().name().equals("FIXED")) {
            discountAmount = promo.getDiscountValue();
        } else if (promo.getDiscountType().name().equals("PERCENT")) {
            discountAmount = request.getOrderTotal().multiply(promo.getDiscountValue().divide(new BigDecimal("100")));
            if (promo.getMaxDiscountAmount() != null && discountAmount.compareTo(promo.getMaxDiscountAmount()) > 0) {
                discountAmount = promo.getMaxDiscountAmount();
            }
        }

        if (discountAmount.compareTo(request.getOrderTotal()) > 0) {
            discountAmount = request.getOrderTotal();
        }

        return new PromotionResponse(discountAmount, "Áp dụng mã thành công!", promo.getImageUrl());
    }

    // --- HÀM 2: CHỐT ĐƠN VÀ LƯU LỊCH SỬ (Gọi khi thanh toán xong) ---
    @Transactional
    public void commitPromotionUsage(String code, Long userId) {
        if (code == null || code.trim().isEmpty()) return;

        Promotion promo = promotionRepository.findByCode(code).orElse(null);
        if (promo == null) return;

        // Tăng số lượt tổng
        promo.setUsageCount(promo.getUsageCount() + 1);
        promotionRepository.save(promo);

        // Xử lý bảng UserPromotion của bạn
        UserPromotion userPromo = userPromotionRepository.findByUserIdAndPromotionId(userId, promo.getId())
                .orElse(new UserPromotion());

        if (userPromo.getId() == null) {
            User user = userRepository.findById(userId).get();
            userPromo.setUser(user);
            userPromo.setPromotion(promo);
            userPromo.setUsedCount((byte) 1);
        } else {
            userPromo.setUsedCount((byte) (userPromo.getUsedCount() + 1));
        }
        userPromotionRepository.save(userPromo);
    }
}