package iuh.wwwprogramming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String productId;
    private String productCode;
    private String productName;
    private String imageUrl;
    private BigDecimal unitPrice;
    private Integer quantity;
    private Integer stockQuantity; // Số lượng tồn kho để hiển thị/kiểm soát
    private BigDecimal subtotal;

    public BigDecimal getSubtotal() {
        if (unitPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
