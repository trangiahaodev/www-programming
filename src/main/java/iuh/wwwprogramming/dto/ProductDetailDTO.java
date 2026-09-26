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
public class ProductDetailDTO {

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
    private String currency;
    private String origin;
    private String description;
    private String ingredients;
    private String usageInstructions;
    private Integer stock;
    private String barcode;
    private Boolean isOutOfStock;
    private Boolean isHot;
    private Boolean isNew;
    private Double rating;
    private Integer reviewCount;
    private Integer soldCount;

    // Dynamic product variants (phù hợp theo từng dòng sản phẩm: Mặt nạ, Son môi, Skincare, Nước hoa...)
    private String variantType;
    private String variantGroupTitle;
    private String variantIcon;
    private java.util.List<ProductVariantDTO> variants;

    public ProductVariantDTO getDefaultVariant() {
        if (variants == null || variants.isEmpty()) return null;
        return variants.stream()
                .filter(v -> Boolean.TRUE.equals(v.getIsDefault()))
                .findFirst()
                .orElse(variants.get(0));
    }

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
