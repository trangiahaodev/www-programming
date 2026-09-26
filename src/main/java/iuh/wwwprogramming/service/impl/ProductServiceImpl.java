package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.ProductCreateDTO;
import iuh.wwwprogramming.dto.ProductResponseDTO;
import iuh.wwwprogramming.dto.ProductUpdateDTO;
import iuh.wwwprogramming.entity.Category;
import iuh.wwwprogramming.entity.Product;
import iuh.wwwprogramming.repository.CategoryRepository;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<ProductResponseDTO> getProducts(String keyword, String categoryId, Pageable pageable) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanCategoryId = (categoryId != null && !categoryId.trim().isEmpty()) ? categoryId.trim() : null;

        return productRepository.searchProducts(cleanKeyword, cleanCategoryId, pageable)
                .map(this::convertToResponseDTO);
    }

    @Override
    public ProductUpdateDTO getProductForEdit(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã định danh sản phẩm không hợp lệ!");
        }

        Product product = productRepository.findByIdWithCategory(id.trim())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm mỹ phẩm không tồn tại trong hệ thống!"));

        return ProductUpdateDTO.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .active(product.getActive())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .build();
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(String id, ProductUpdateDTO dto) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã định danh sản phẩm không hợp lệ!");
        }

        String cleanId = id.trim();
        String name = dto.getName() != null ? dto.getName().trim() : "";
        String categoryId = dto.getCategoryId() != null ? dto.getCategoryId().trim() : "";

        // 1. Tìm sản phẩm hiện tại trong CSDL
        Product product = productRepository.findById(cleanId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm mỹ phẩm không tồn tại trong hệ thống!"));

        // 2. Kiểm tra trùng tên sản phẩm với các sản phẩm khác (loại trừ chính nó)
        if (productRepository.existsByNameAndIdNot(name, cleanId)) {
            throw new IllegalArgumentException("Tên sản phẩm '" + name + "' đã được sử dụng bởi sản phẩm khác!");
        }

        // 3. Kiểm tra danh mục mới có tồn tại trong CSDL
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục mỹ phẩm được chọn không tồn tại trong hệ thống!"));

        // 4. Cập nhật các trường thông tin (giữ nguyên productCode - Business Key bất biến)
        product.setName(name);
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setDescription(dto.getDescription() != null && !dto.getDescription().trim().isEmpty() ? dto.getDescription().trim() : null);
        product.setImageUrl(dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty() ? dto.getImageUrl().trim() : null);
        product.setActive(dto.getActive() != null ? dto.getActive() : true);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return convertToResponseDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductCreateDTO dto) {
        String productCode = dto.getProductCode() != null ? dto.getProductCode().trim() : "";
        String name = dto.getName() != null ? dto.getName().trim() : "";
        String categoryId = dto.getCategoryId() != null ? dto.getCategoryId().trim() : "";

        // 1. Kiểm tra trùng mã sản phẩm (Business Key)
        if (productRepository.existsByProductCode(productCode)) {
            throw new IllegalArgumentException("Mã sản phẩm '" + productCode + "' đã tồn tại trong hệ thống!");
        }

        // 2. Kiểm tra trùng tên sản phẩm
        if (productRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tên sản phẩm '" + name + "' đã tồn tại trong hệ thống!");
        }

        // 3. Kiểm tra danh mục có thực sự tồn tại trong CSDL
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục mỹ phẩm được chọn không tồn tại trong hệ thống!"));

        // 4. Tạo entity qua Builder
        Product product = Product.builder()
                .productCode(productCode)
                .name(name)
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .description(dto.getDescription() != null && !dto.getDescription().trim().isEmpty() ? dto.getDescription().trim() : null)
                .imageUrl(dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty() ? dto.getImageUrl().trim() : null)
                .active(dto.getActive() != null ? dto.getActive() : true)
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);
        return convertToResponseDTO(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã định danh sản phẩm không hợp lệ!");
        }

        Product product = productRepository.findById(id.trim())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm mỹ phẩm cần xóa!"));

        productRepository.delete(product);
    }

    private ProductResponseDTO convertToResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .active(product.getActive())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .createdAt(product.getCreatedAt())
                .build();
    }
}
