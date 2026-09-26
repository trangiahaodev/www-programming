package iuh.wwwprogramming.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDTO {

    @NotBlank(message = "Mã danh mục không được để trống")
    @Size(min = 2, max = 20, message = "Mã danh mục phải từ 2 đến 20 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Mã danh mục chỉ được chứa chữ cái, chữ số, dấu gạch ngang hoặc gạch dưới (không chứa khoảng trắng)")
    private String categoryCode;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 2, max = 150, message = "Tên danh mục phải từ 2 đến 150 ký tự")
    private String name;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;

    @Builder.Default
    private Boolean active = true;
}
