package iuh.wwwprogramming.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderQuantityUpdateRequestDTO {

    @NotBlank(message = "Mã đơn hàng không được để trống")
    @Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        message = "Mã đơn hàng không đúng định dạng UUID"
    )
    private String orderId;

    @NotNull(message = "Danh sách cập nhật không được null")
    @NotEmpty(message = "Danh sách cập nhật không được để trống")
    @Valid
    @Builder.Default
    private List<OrderItemQuantityUpdateDTO> items = new ArrayList<>();
}
