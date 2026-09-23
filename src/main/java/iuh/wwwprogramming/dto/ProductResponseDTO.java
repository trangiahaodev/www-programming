package iuh.wwwprogramming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {
    private String id;
    private String productCode;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private String description;
    private Boolean active;
    private String categoryName;
}
