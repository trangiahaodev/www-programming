package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.CategoryCreateDTO;
import iuh.wwwprogramming.dto.CategoryResponseDTO;
import iuh.wwwprogramming.dto.CategoryUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    Page<CategoryResponseDTO> getCategories(String keyword, Pageable pageable);

    CategoryResponseDTO createCategory(CategoryCreateDTO dto);

    CategoryUpdateDTO getCategoryForUpdate(String id);

    CategoryResponseDTO updateCategory(String id, CategoryUpdateDTO dto);

    void deleteCategory(String id);

    java.util.List<CategoryResponseDTO> getActiveCategories();
}
