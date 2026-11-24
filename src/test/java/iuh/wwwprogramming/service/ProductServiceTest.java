package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.ProductCreateDTO;
import iuh.wwwprogramming.entity.Product;
import iuh.wwwprogramming.repository.CategoryRepository;
import iuh.wwwprogramming.repository.OrderItemRepository;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("deleteProduct should perform Soft Delete by setting isDeleted = true and calling save, never physical delete")
    void deleteProduct_ShouldSoftDelete() {
        String productId = "prod-123";
        Product product = Product.builder()
                .id(productId)
                .productCode("SP26000001")
                .name("Son Dưỡng")
                .price(BigDecimal.valueOf(100000))
                .stockQuantity(10)
                .isDeleted(false)
                .build();

        when(productRepository.findByIdAndIsDeletedFalse(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        productService.deleteProduct(productId);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        verify(productRepository, never()).delete(any());
        verify(productRepository, never()).deleteById(any());

        assertThat(captor.getValue().getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("deleteProduct should throw IllegalArgumentException when product does not exist or already soft deleted")
    void deleteProduct_NotFound_ShouldThrowException() {
        String productId = "prod-not-found";
        when(productRepository.findByIdAndIsDeletedFalse(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(productId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Không tìm thấy sản phẩm");

        verify(productRepository, never()).delete(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("createProduct should fail if product code already exists among non-deleted products")
    void createProduct_DuplicateCode_ShouldThrowException() {
        ProductCreateDTO dto = ProductCreateDTO.builder()
                .productCode("SP26000001")
                .name("Kem dưỡng")
                .categoryId("cat-1")
                .price(BigDecimal.valueOf(50000))
                .stockQuantity(5)
                .build();

        when(productRepository.existsByProductCodeAndIsDeletedFalse("SP26000001")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Mã sản phẩm");

        verify(productRepository, never()).save(any());
    }
}
