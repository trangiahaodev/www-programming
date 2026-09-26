package iuh.wwwprogramming.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDeleteDTO {
    @NotNull @AssertTrue(message="Vui lòng xác nhận trước khi xóa") private Boolean confirmed;
    @Size(max=100) @Builder.Default private String keyword = "";
    @Min(0) @Builder.Default private int page = 0;
    @Min(1) @Max(100) @Builder.Default private int size = 10;
}
