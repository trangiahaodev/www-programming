package iuh.wwwprogramming.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserUpdateDTO {
    @NotBlank(message="Vui lòng nhập họ tên") @Size(max=100) private String fullName;
    @NotBlank(message="Vui lòng nhập email") @Email(message="Email không đúng định dạng") @Size(max=100) private String email;
    @Size(max=20) @Pattern(regexp="[+0-9 ()-]*", message="Số điện thoại không hợp lệ") private String phone;
    @Size(max=255) private String address;
    @NotNull(message="Vui lòng chọn trạng thái") private Boolean active;
}
