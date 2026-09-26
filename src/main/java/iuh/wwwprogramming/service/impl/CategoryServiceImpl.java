package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.CategoryCreateDTO;
import iuh.wwwprogramming.dto.CategoryResponseDTO;
import iuh.wwwprogramming.dto.CategoryUpdateDTO;
import iuh.wwwprogramming.entity.Category;
import iuh.wwwprogramming.repository.CategoryRepository;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

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

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryCreateDTO dto) {
        String categoryCode = dto.getCategoryCode() != null ? dto.getCategoryCode().trim() : "";
        String name = dto.getName() != null ? dto.getName().trim() : "";

        if (categoryRepository.existsByCategoryCode(categoryCode)) {
            throw new IllegalArgumentException("Mã danh mục '" + categoryCode + "' đã tồn tại trong hệ thống!");
        }

        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tên danh mục '" + name + "' đã tồn tại trong hệ thống!");
        }

        Category category = Category.builder()
                .categoryCode(categoryCode)
                .name(name)
                .description(dto.getDescription() != null && !dto.getDescription().trim().isEmpty() ? dto.getDescription().trim() : null)
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();

        Category savedCategory = categoryRepository.save(category);
        return convertToResponseDTO(savedCategory);
    }

    @Override
    public CategoryUpdateDTO getCategoryForUpdate(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục mỹ phẩm cần cập nhật!"));

        return CategoryUpdateDTO.builder()
                .id(category.getId())
                .categoryCode(category.getCategoryCode())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .build();
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(String id, CategoryUpdateDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục mỹ phẩm cần cập nhật!"));

        String name = dto.getName() != null ? dto.getName().trim() : "";

        if (categoryRepository.existsByNameAndIdNot(name, id)) {
            throw new IllegalArgumentException("Tên danh mục '" + name + "' đã tồn tại trong hệ thống! Vui lòng chọn tên khác.");
        }

        category.setName(name);
        category.setDescription(dto.getDescription() != null && !dto.getDescription().trim().isEmpty() ? dto.getDescription().trim() : null);
        category.setActive(dto.getActive() != null ? dto.getActive() : true);
        // Note: categoryCode is immutable Business Key and is never modified

        Category updatedCategory = categoryRepository.save(category);
        return convertToResponseDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục mỹ phẩm cần xóa!"));

        // Kiểm tra điều kiện ràng buộc: Nếu danh mục đang chứa sản phẩm thì không cho phép xóa
        long productCount = countProductsByCategoryId(id);
        if (productCount > 0) {
            throw new IllegalStateException("Không thể xóa danh mục '" + category.getName() + "' vì đang có " + productCount + " sản phẩm thuộc danh mục này! Vui lòng xóa hoặc chuyển sản phẩm sang danh mục khác trước.");
        }

        categoryRepository.delete(category);
    }

    private long countProductsByCategoryId(String categoryId) {
        return productRepository.countByCategoryId(categoryId);
    }

    @Override
    public List<CategoryResponseDTO> getActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    private CategoryResponseDTO convertToResponseDTO(Category category) {
        long count = productRepository.countByCategoryId(category.getId());
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .categoryCode(category.getCategoryCode())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .productCount(count)
                .createdAt(category.getCreatedAt())
                .build();
    }
}
