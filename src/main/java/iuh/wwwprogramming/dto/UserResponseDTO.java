package iuh.wwwprogramming.dto;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponseDTO {
    private String id;
    private String userCode;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private Boolean active;
}
