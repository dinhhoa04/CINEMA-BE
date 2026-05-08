package cinema.controller;

import cinema.dto.request.FoodRequest;
import cinema.entity.FoodCategory;
import cinema.entity.FoodItem;
import cinema.repository.FoodCategoryRepository;
import cinema.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/foods")
@CrossOrigin("*")
public class FoodController {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private FoodCategoryRepository foodCategoryRepository;

    // 1. Dành cho Khách hàng (Chỉ lấy món đang bán)
    @GetMapping("/active")
    public ResponseEntity<List<FoodItem>> getActiveFoods() {
        return ResponseEntity.ok(foodItemRepository.findByIsAvailableTrue());
    }

    // 2. Dành cho Admin (Lấy tất cả)
    @GetMapping
    public ResponseEntity<List<FoodItem>> getAllFoods() {
        return ResponseEntity.ok(foodItemRepository.findAll());
    }

    // 3. API cung cấp danh sách Danh mục cho thẻ <select> trong Form React
    @GetMapping("/categories")
    public ResponseEntity<List<FoodCategory>> getCategories() {
        return ResponseEntity.ok(foodCategoryRepository.findAll());
    }

    // 4. Thêm đồ ăn mới
    @PostMapping
    public ResponseEntity<?> createFood(@RequestBody FoodRequest request) {
        FoodItem food = new FoodItem();
        mapRequestToEntity(request, food);
        food.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(foodItemRepository.save(food));
    }

    // 5. Cập nhật đồ ăn
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFood(@PathVariable Long id, @RequestBody FoodRequest request) {
        FoodItem food = foodItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn"));
        mapRequestToEntity(request, food);
        return ResponseEntity.ok(foodItemRepository.save(food));
    }

    // 6. Xóa đồ ăn
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFood(@PathVariable Long id) {
        foodItemRepository.deleteById(id);
        return ResponseEntity.ok("Xóa thành công");
    }

    // Hàm phụ trợ Map dữ liệu
    private void mapRequestToEntity(FoodRequest request, FoodItem food) {
        food.setName(request.getName());
        food.setDescription(request.getDescription());
        food.setPrice(request.getPrice());
        food.setImageUrl(request.getImageUrl());
        food.setIsAvailable(request.getIsAvailable());
        food.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

        // Map Category từ categoryId gửi lên
        if (request.getCategoryId() != null) {
            FoodCategory category = foodCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
            food.setCategory(category);
        }
    }
}