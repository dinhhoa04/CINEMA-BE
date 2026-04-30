package cinema.exception;

import cinema.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Bắt các lỗi tự định nghĩa (Ví dụ: Sai mật khẩu, User không tồn tại...)
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handlingAppException(AppException exception) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setCode(exception.getCode());
        apiResponse.setMessage(exception.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // 2. Bắt các lỗi Validation từ DTO (Ví dụ: Bỏ trống email, sai định dạng...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handlingValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        // Trích xuất từng trường bị lỗi và thông báo lỗi
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>();
        apiResponse.setCode(400);
        apiResponse.setMessage("Dữ liệu đầu vào không hợp lệ");
        apiResponse.setData(errors);

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // 3. Bắt tất cả các lỗi hệ thống "bất thình lình" (NullPointer, đứt cáp DB...)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handlingRuntimeException(Exception exception) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setCode(500);
        apiResponse.setMessage("Lỗi hệ thống cục bộ: " + exception.getMessage()); // Khi lên Production nên ẩn Message này đi

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}