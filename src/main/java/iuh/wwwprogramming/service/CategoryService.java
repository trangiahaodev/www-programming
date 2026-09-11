package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.CategoryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryResponseDTO> getCategories(String keyword, Pageable pageable);
}
