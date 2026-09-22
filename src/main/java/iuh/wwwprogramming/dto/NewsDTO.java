package iuh.wwwprogramming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsDTO {
    private Integer id;
    private String slug;
    private String title;
    private String excerpt;
    private String content;
    private String date;
    private String category;
    private String author;
    private String readTime;
    private String image;
    private List<ProductCardDTO> linkedProducts;
}
