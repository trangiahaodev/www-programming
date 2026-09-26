package iuh.wwwprogramming.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {

    @NotBlank(message = "Mã sản phẩm không được để trống")
    @Size(min = 3, max = 20, message = "Mã sản phẩm phải từ 3 đến 20 ký tự")
    private String productCode;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 150, message = "Tên sản phẩm phải từ 2 đến 150 ký tự")
    private String name;

    @NotBlank(message = "Vui lòng chọn danh mục mỹ phẩm")
    private String categoryId;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.01", inclusive = true, message = "Giá bán phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 0")
    private Integer stockQuantity;

    @Size(max = 2000, message = "Mô tả sản phẩm tối đa 2000 ký tự")
    private String description;

    @Size(max = 500, message = "Đường dẫn hình ảnh tối đa 500 ký tự")
    private String imageUrl;

    @Builder.Default
    private Boolean active = true;
}
