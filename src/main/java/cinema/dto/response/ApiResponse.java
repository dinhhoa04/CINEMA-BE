package cinema.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Biến nào null thì ẩn luôn khỏi cục JSON
public class ApiResponse<T> {
    private int code = 200;
    private String message = "Success";
    private T data;
}