package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.ProductResponseDTO;
import iuh.wwwprogramming.entity.Category;
import iuh.wwwprogramming.entity.Product;
import iuh.wwwprogramming.repository.CategoryRepository;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<ProductResponseDTO> getActiveProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDTO getProductById(String id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm hoặc sản phẩm đã ngừng kinh doanh!"));
        return mapToDTO(product);
    }

    @Override
    @Transactional
    public void initSampleProductsIfEmpty() {
        if (productRepository.count() == 0) {
            Category defaultCategory = categoryRepository.findAll().stream().findFirst().orElseGet(() -> {
                Category cat = Category.builder()
                        .categoryCode("CAT26000001")
                        .name("Chăm sóc da mặt")
                        .description("Các sản phẩm chăm sóc da chuyên sâu")
                        .active(true)
                        .build();
                return categoryRepository.save(cat);
            });

            Product p1 = Product.builder()
                    .productCode("SP26000001")
                    .name("Serum Dưỡng Sáng Da Vitamin C")
                    .price(new BigDecimal("350000"))
                    .stockQuantity(50)
                    .imageUrl("https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500")
                    .description("Serum dưỡng ẩm, làm mờ thâm và sáng đều màu da tự nhiên.")
                    .active(true)
                    .category(defaultCategory)
                    .build();

            Product p2 = Product.builder()
                    .productCode("SP26000002")
                    .name("Kem Chống Nắng Phổ Rộng SPF 50+")
                    .price(new BigDecimal("280000"))
                    .stockQuantity(30)
                    .imageUrl("https://images.unsplash.com/photo-1556228720-195a672e8a03?w=500")
                    .description("Bảo vệ da toàn diện khỏi tia UVA/UVB, kiềm dầu mỏng nhẹ.")
                    .active(true)
                    .category(defaultCategory)
                    .build();

            Product p3 = Product.builder()
                    .productCode("SP26000003")
                    .name("Sữa Rửa Mặt Dịu Nhẹ Cân Bằng pH")
                    .price(new BigDecimal("190000"))
                    .stockQuantity(80)
                    .imageUrl("https://images.unsplash.com/photo-1556228722-d0b5de7317e6?w=500")
                    .description("Làm sạch sâu bã nhờn mà không gây khô căng da.")
                    .active(true)
                    .category(defaultCategory)
                    .build();

            productRepository.saveAll(List.of(p1, p2, p3));
        }
    }

    private ProductResponseDTO mapToDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .description(product.getDescription())
                .active(product.getActive())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "")
                .build();
    }
}
