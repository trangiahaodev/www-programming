package iuh.wwwprogramming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDTO {
    private String id;              // ID biến thể
    private String name;            // Tên ngắn gọn hiển thị (vd: "1 Miếng", "#01 Đỏ Cam", "50ml")
    private String subName;         // Mô tả phụ (vd: "Dùng thử", "Chuẩn hãng", "Tiết kiệm 20%")
    private String fullLabel;       // Nhãn đầy đủ (vd: "Hộp 5 miếng (Chuẩn hãng - Bán chạy)")
    private Double priceMultiplier; // Hệ số giá
    private Double price;           // Giá sau khi áp dụng hệ số
    private String colorHex;        // Mã màu HEX (dành cho son môi, cushion, tone màu)
    private String badge;           // Huy hiệu nổi bật (vd: "Bán chạy", "Tiết kiệm 15%", "HOT")
    private Integer stock;          // Số lượng tồn kho biến thể
    private Boolean isDefault;      // Biến thể mặc định được chọn

    public String getFormattedPrice() {
        if (price == null) return "0₫";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(price) + "₫";
    }
}
