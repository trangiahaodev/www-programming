package iuh.wwwprogramming.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemQuantityUpdateDTO {

    @NotBlank(message = "Mã chi tiết đơn hàng không được để trống")
    @Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        message = "Mã chi tiết đơn hàng không đúng định dạng UUID"
    )
    private String orderItemId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng sản phẩm phải lớn hơn hoặc bằng 1")
    @Max(value = 999, message = "Số lượng sản phẩm không được vượt quá 999 món")
    private Integer quantity;
}
