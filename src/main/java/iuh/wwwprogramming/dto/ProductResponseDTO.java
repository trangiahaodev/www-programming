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
    private String imageUrl;
    private Boolean active;
    private String categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
}
