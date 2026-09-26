package iuh.wwwprogramming.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.nio.charset.StandardCharsets;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserRegisterDTO {
    @NotBlank(message="Vui lòng nhập họ tên") @Size(max=100)
    private String fullName;
    @NotBlank(message="Vui lòng nhập email") @Email(message="Email không đúng định dạng") @Size(max=100)
    private String email;
    @NotBlank @Size(min=8, max=72, message="Mật khẩu cần từ 8 đến 72 ký tự") @ToString.Exclude
    private String password;
    @NotBlank(message="Vui lòng xác nhận mật khẩu") @ToString.Exclude
    private String confirmPassword;
    @AssertTrue(message="Mật khẩu xác nhận không khớp")
    public boolean isPasswordMatching() { return password != null && password.equals(confirmPassword); }
    @AssertTrue(message="Mật khẩu tối đa 72 byte UTF-8")
    public boolean isPasswordWithinLimit() { return password == null || password.getBytes(StandardCharsets.UTF_8).length <= 72; }
}
