package iuh.wwwprogramming.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private String id;
    private String productCode;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private String description;

    // Thuộc tính này V2 gọi là imageUrl, đã trùng khớp với V1
    private String imageUrl;

    private Boolean active;

    // Hai trường mở rộng phục vụ cho UI của nhánh Core/Admin
    private String categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
}