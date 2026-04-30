package cinema.controller; // Đổi lại package cho khớp project của anh

import cinema.entity.FoodItem;
import cinema.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/foods")
@CrossOrigin("*")
public class FoodController {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @GetMapping("/active")
    public ResponseEntity<List<FoodItem>> getActiveFoods() {
        // Lấy tất cả đồ ăn có is_available = 1 từ DB và trả về cho React
        List<FoodItem> activeFoods = foodItemRepository.findByIsAvailableTrue();
        return ResponseEntity.ok(activeFoods);
    }
}