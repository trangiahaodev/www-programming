package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.CategoryCreateDTO;
import iuh.wwwprogramming.entity.Category;
import iuh.wwwprogramming.repository.CategoryRepository;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("deleteCategory should perform Soft Delete by setting isDeleted = true and calling save, never physical delete")
    void deleteCategory_ShouldSoftDelete() {
        String categoryId = "cat-123";
        Category category = Category.builder()
                .id(categoryId)
                .categoryCode("CAT001")
                .name("Chăm sóc da")
                .isDeleted(false)
                .build();

        when(categoryRepository.findByIdAndIsDeletedFalse(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryIdAndIsDeletedFalse(categoryId)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        categoryService.deleteCategory(categoryId);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        verify(categoryRepository, never()).delete(any());
        verify(categoryRepository, never()).deleteById(any());

        assertThat(captor.getValue().getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("deleteCategory should throw IllegalStateException if category has active products")
    void deleteCategory_HasProducts_ShouldThrowIllegalStateException() {
        String categoryId = "cat-123";
        Category category = Category.builder()
                .id(categoryId)
                .categoryCode("CAT001")
                .name("Chăm sóc da")
                .isDeleted(false)
                .build();

        when(categoryRepository.findByIdAndIsDeletedFalse(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryIdAndIsDeletedFalse(categoryId)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory(categoryId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("đang có sản phẩm");

        verify(categoryRepository, never()).save(any());
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteCategory should throw IllegalArgumentException when category not found")
    void deleteCategory_NotFound_ShouldThrowException() {
        String categoryId = "cat-not-found";
        when(categoryRepository.findByIdAndIsDeletedFalse(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(categoryId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Không tìm thấy danh mục");

        verify(categoryRepository, never()).save(any());
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("createCategory should fail if category code already exists among non-deleted categories")
    void createCategory_DuplicateCode_ShouldThrowException() {
        CategoryCreateDTO dto = CategoryCreateDTO.builder()
                .categoryCode("CAT001")
                .name("Trang điểm")
                .build();

        when(categoryRepository.existsByCategoryCodeAndIsDeletedFalse("CAT001")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Mã danh mục");

        verify(categoryRepository, never()).save(any());
    }
}
