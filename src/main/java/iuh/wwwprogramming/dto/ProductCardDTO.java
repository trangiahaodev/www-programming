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
public class ProductCardDTO {

    private String id;
    private String productCode;
    private String name;
    private String brand;
    private String categoryId;
    private String categoryName;
    private String image;
    private Double price;
    private Integer discount;
    private Double originalPrice;
    private Integer stock;
    private Boolean isOutOfStock;
    private Boolean isHot;
    private Boolean isNew;
    private Double rating;
    private Integer reviewCount;
    private Integer soldCount;

    public String getFormattedPrice() {
        if (price == null) return "0₫";
        DecimalFormat formatter = getCurrencyFormatter();
        return formatter.format(price) + "₫";
    }

    public String getFormattedOriginalPrice() {
        if (originalPrice == null || originalPrice <= 0) return null;
        DecimalFormat formatter = getCurrencyFormatter();
        return formatter.format(originalPrice) + "₫";
    }

    private static DecimalFormat getCurrencyFormatter() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        return new DecimalFormat("#,###", symbols);
    }
}
