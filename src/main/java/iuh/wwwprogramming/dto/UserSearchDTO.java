package iuh.wwwprogramming.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserSearchDTO {
    @Size(max=100, message="Từ khóa tối đa 100 ký tự") @Builder.Default
    private String keyword = "";
    @Min(0) @Builder.Default private int page = 0;
    @Min(1) @Max(100) @Builder.Default private int size = 10;
}
