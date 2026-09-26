package iuh.wwwprogramming.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequestDTO {

    @NotBlank(message = "Họ tên người nhận không được để trống")
    @Size(max = 100, message = "Họ tên người nhận không vượt quá 100 ký tự")
    private String recipientName;

    @NotBlank(message = "Số điện thoại nhận hàng không được để trống")
    @Pattern(
        regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$",
        message = "Số điện thoại không đúng định dạng Việt Nam (VD: 0912345678)"
    )
    private String recipientPhone;

    @NotBlank(message = "Địa chỉ nhận hàng không được để trống")
    @Size(max = 255, message = "Địa chỉ nhận hàng không vượt quá 255 ký tự")
    private String shippingAddress;

    @NotBlank(message = "Vui lòng chọn phương thức thanh toán")
    @Builder.Default
    private String paymentMethod = "COD";

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String note;
}
