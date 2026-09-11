package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.CategoryResponseDTO;
import iuh.wwwprogramming.repository.CategoryRepository;
import iuh.wwwprogramming.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return categoryRepository.searchCategories(searchKeyword, pageable);
    }
}
