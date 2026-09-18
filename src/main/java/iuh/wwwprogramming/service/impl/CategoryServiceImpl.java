package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.CategoryResponseDTO;
import iuh.wwwprogramming.entity.Category;
import iuh.wwwprogramming.repository.CategoryRepository;
import iuh.wwwprogramming.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Page<CategoryResponseDTO> getCategories(String keyword, Pageable pageable) {
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Pageable sortedPageable = (pageable != null && pageable.getSort().isSorted())
                ? pageable
                : PageRequest.of(
                        pageable != null ? pageable.getPageNumber() : 0,
                        pageable != null ? pageable.getPageSize() : 10,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                );

        Page<Category> categoryPage = categoryRepository.searchCategories(searchKeyword, sortedPageable);
        return categoryPage.map(this::convertToResponseDTO);
    }

    private CategoryResponseDTO convertToResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .categoryCode(category.getCategoryCode())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .productCount(0L)
                .createdAt(category.getCreatedAt())
                .build();
    }
}
