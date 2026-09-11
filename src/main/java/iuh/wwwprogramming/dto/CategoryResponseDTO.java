package iuh.wwwprogramming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    private String id;
    private String categoryCode;
    private String name;
    private String description;
    private Boolean active;
    private Long productCount;
    private LocalDateTime createdAt;
}
