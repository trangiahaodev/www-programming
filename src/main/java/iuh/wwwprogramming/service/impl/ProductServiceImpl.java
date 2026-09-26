package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.ProductCardDTO;
import iuh.wwwprogramming.dto.ProductCreateDTO;
import iuh.wwwprogramming.dto.ProductDetailDTO;
import iuh.wwwprogramming.dto.ProductResponseDTO;
import iuh.wwwprogramming.dto.ProductUpdateDTO;
import iuh.wwwprogramming.dto.ProductVariantDTO;
import iuh.wwwprogramming.entity.Category;
import iuh.wwwprogramming.entity.Product;
import iuh.wwwprogramming.repository.CategoryRepository;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // =========================================================================
    // 1. NHÓM HÀM CHO ADMIN (Quản lý CRUD - Dữ liệu thô, không lọc active)
    // =========================================================================

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
                .imageUrl(product.getImage())
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

        // 4. Cập nhật các trường thông tin
        product.setName(name);
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setDescription(dto.getDescription() != null && !dto.getDescription().trim().isEmpty() ? dto.getDescription().trim() : null);
        product.setImage(dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty() ? dto.getImageUrl().trim() : null);
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

        if (productRepository.existsByProductCode(productCode)) {
            throw new IllegalArgumentException("Mã sản phẩm '" + productCode + "' đã tồn tại trong hệ thống!");
        }
        if (productRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tên sản phẩm '" + name + "' đã tồn tại trong hệ thống!");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục mỹ phẩm được chọn không tồn tại trong hệ thống!"));

        Product product = Product.builder()
                .productCode(productCode)
                .name(name)
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .description(dto.getDescription() != null && !dto.getDescription().trim().isEmpty() ? dto.getDescription().trim() : null)
                .image(dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty() ? dto.getImageUrl().trim() : null)
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


    // =========================================================================
    // 2. NHÓM HÀM CHO CUSTOMER (Giao diện mua sắm - Bắt buộc lọc active = true)
    // =========================================================================

    @Override
    public Page<ProductCardDTO> searchProducts(String keyword, String categoryId, String sortBy, Pageable pageable) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanCategory = (categoryId != null && !categoryId.trim().isEmpty() && !"all".equalsIgnoreCase(categoryId.trim()))
                ? categoryId.trim()
                : null;

        Sort sort = determineSort(sortBy);
        int pageNumber = (pageable != null) ? Math.max(0, pageable.getPageNumber()) : 0;
        int pageSize = (pageable != null && pageable.getPageSize() > 0) ? pageable.getPageSize() : 12;

        Pageable sortedPageable = PageRequest.of(pageNumber, pageSize, sort);

        // ĐÃ SỬA: Gọi đúng hàm của Customer
        Page<Product> productPage = productRepository.searchCustomerProducts(cleanKeyword, cleanCategory, sortedPageable);
        return productPage.map(this::convertToCardDTO);
    }

    @Override
    public ProductDetailDTO getProductDetail(String idOrCode) {
        if (idOrCode == null || idOrCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hoặc định danh sản phẩm không hợp lệ!");
        }

        // ĐÃ SỬA: Gọi đúng hàm findActiveById của Customer
        Product product = productRepository.findActiveByIdWithCategory(idOrCode.trim())
                .or(() -> productRepository.findByProductCodeWithCategory(idOrCode.trim()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm yêu cầu: " + idOrCode));

        return convertToDetailDTO(product);
    }

    @Override
    public List<ProductCardDTO> getRelatedProducts(String categoryId, String excludeProductId, int limit) {
        if (categoryId == null || categoryId.trim().isEmpty()) {
            return List.of();
        }
        Pageable pageable = PageRequest.of(0, Math.max(1, limit), Sort.by(Sort.Direction.DESC, "soldCount", "createdAt"));
        return productRepository.findRelatedProducts(categoryId.trim(), excludeProductId, pageable)
                .stream()
                .map(this::convertToCardDTO)
                .toList();
    }

    @Override
    public List<ProductCardDTO> getHotProducts(int limit) {
        Pageable pageable = PageRequest.of(0, Math.max(1, limit), Sort.by(Sort.Direction.DESC, "discount", "soldCount"));
        return productRepository.findHotProducts(pageable)
                .stream()
                .map(this::convertToCardDTO)
                .toList();
    }

    @Override
    public List<ProductCardDTO> getNewProducts(int limit) {
        Pageable pageable = PageRequest.of(0, Math.max(1, limit), Sort.by(Sort.Direction.DESC, "createdAt"));
        return productRepository.findNewProducts(pageable)
                .stream()
                .map(this::convertToCardDTO)
                .toList();
    }

    @Override
    public List<ProductCardDTO> getFeaturedBrandProducts(int limit) {
        Pageable pageable = PageRequest.of(0, 60, Sort.by(Sort.Direction.DESC, "soldCount", "rating"));
        List<Product> products = new ArrayList<>(productRepository.findFeaturedBrandProducts(pageable));
        if (products.isEmpty() || products.size() < limit) {
            products = new ArrayList<>(productRepository.findAll(PageRequest.of(0, 60)).getContent());
        }
        Collections.shuffle(products);

        List<Product> selected = new ArrayList<>();
        Set<String> selectedBrands = new HashSet<>();
        for (Product p : products) {
            if (p.getBrand() != null && !p.getBrand().isBlank() && !selectedBrands.contains(p.getBrand().trim())) {
                selected.add(p);
                selectedBrands.add(p.getBrand().trim());
                if (selected.size() >= limit) break;
            }
        }
        if (selected.size() < limit) {
            for (Product p : products) {
                if (!selected.contains(p)) {
                    selected.add(p);
                    if (selected.size() >= limit) break;
                }
            }
        }

        return selected.stream()
                .map(this::convertToCardDTO)
                .toList();
    }

    @Override
    public List<String> getTopBrands() {
        return productRepository.findDistinctBrands();
    }


    // =========================================================================
    // 3. NHÓM HÀM TIỆN ÍCH (MAPPING & HELPERS)
    // =========================================================================

    private ProductResponseDTO convertToResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .description(product.getDescription())
                .imageUrl(product.getImage()) // Hỗ trợ trường imageUrl của form Admin
                .active(product.getActive())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .createdAt(product.getCreatedAt())
                .build();
    }

    private Sort determineSort(String sortBy) {
        if (sortBy == null) {
            return Sort.by(Sort.Direction.DESC, "soldCount", "createdAt");
        }
        return switch (sortBy.toLowerCase()) {
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "hot" -> Sort.by(Sort.Direction.DESC, "discount", "soldCount");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "soldCount", "createdAt");
        };
    }

    private ProductCardDTO convertToCardDTO(Product product) {
        int discount = product.getDiscount() != null ? product.getDiscount() : 0;

        // ĐÃ FIX: Chuyển BigDecimal của Entity sang Double cho Customer DTO
        Double price = product.getPrice() != null ? product.getPrice().doubleValue() : 0.0;

        Double originalPrice = null;
        if (discount > 0 && price > 0) {
            originalPrice = (double) Math.round(price / (1.0 - (discount / 100.0)));
        }

        int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

        return ProductCardDTO.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .brand(product.getBrand())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "")
                .image(product.getImage() != null ? product.getImage() : product.getImageUrl())
                .price(price)
                .discount(discount)
                .originalPrice(originalPrice)
                .stock(stock)
                .isOutOfStock(stock <= 0)
                .isHot(product.getIsHot() != null && product.getIsHot())
                .isNew(product.getIsNew() != null && product.getIsNew())
                .rating(product.getRating() != null ? product.getRating() : 5.0)
                .reviewCount(product.getReviewCount() != null ? product.getReviewCount() : 0)
                .soldCount(product.getSoldCount() != null ? product.getSoldCount() : 0)
                .build();
    }

    private ProductDetailDTO convertToDetailDTO(Product product) {
        int discount = product.getDiscount() != null ? product.getDiscount() : 0;

        // ĐÃ FIX: Chuyển BigDecimal của Entity sang Double cho Customer DTO
        Double price = product.getPrice() != null ? product.getPrice().doubleValue() : 0.0;

        Double originalPrice = null;
        if (discount > 0 && price > 0) {
            originalPrice = (double) Math.round(price / (1.0 - (discount / 100.0)));
        }

        int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

        ProductDetailDTO dto = ProductDetailDTO.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .brand(product.getBrand())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "")
                .image(product.getImage() != null ? product.getImage() : product.getImageUrl())
                .price(price) // Nhận kiểu Double
                .discount(discount)
                .originalPrice(originalPrice)
                .currency(product.getCurrency() != null ? product.getCurrency() : "VND")
                .origin(product.getOrigin())
                .description(product.getDescription())
                .ingredients(product.getIngredients())
                .usageInstructions(product.getUsageInstructions())
                .stock(stock)
                .barcode(product.getBarcode())
                .isOutOfStock(stock <= 0)
                .isHot(product.getIsHot() != null && product.getIsHot())
                .isNew(product.getIsNew() != null && product.getIsNew())
                .rating(product.getRating() != null ? product.getRating() : 5.0)
                .reviewCount(product.getReviewCount() != null ? product.getReviewCount() : 0)
                .soldCount(product.getSoldCount() != null ? product.getSoldCount() : 0)
                .build();

        populateProductVariants(dto, product);
        return dto;
    }

    private void populateProductVariants(ProductDetailDTO dto, Product product) {
        String name = (product.getName() != null) ? product.getName().toLowerCase() : "";
        String catName = (product.getCategory() != null && product.getCategory().getName() != null)
                ? product.getCategory().getName().toLowerCase() : "";
        String combined = name + " " + catName;

        double basePrice = (dto.getPrice() != null) ? dto.getPrice() : 0.0;
        int baseStock = (dto.getStock() != null) ? dto.getStock() : 50;

        List<ProductVariantDTO> variants = new ArrayList<>();

        if (combined.contains("mặt nạ") || combined.contains("mat na") || combined.contains("mask")) {
            dto.setVariantType("PACK");
            dto.setVariantGroupTitle("Quy cách đóng gói:");
            dto.setVariantIcon("box");

            variants.add(ProductVariantDTO.builder()
                    .id("piece-1")
                    .name("1 Miếng")
                    .subName("Dùng thử")
                    .fullLabel("1 Miếng (Dùng thử trải nghiệm)")
                    .priceMultiplier(1.0)
                    .price(basePrice)
                    .stock(baseStock)
                    .isDefault(true)
                    .build());
            // ... (các biến thể khác được giữ nguyên cấu trúc)
            variants.add(ProductVariantDTO.builder()
                    .id("box-5")
                    .name("Hộp 5 miếng")
                    .subName("Chuẩn hãng")
                    .fullLabel("Hộp 5 miếng (Chuẩn hãng - Bán chạy)")
                    .priceMultiplier(4.8)
                    .price(roundPrice(basePrice * 4.8))
                    .badge("Bán chạy")
                    .stock(Math.max(5, baseStock / 5))
                    .isDefault(false)
                    .build());
        } else if (combined.contains("son") || combined.contains("lipstick") || combined.contains("tint") || combined.contains("lip")) {
            dto.setVariantType("COLOR");
            dto.setVariantGroupTitle("Tone màu thời thượng:");
            dto.setVariantIcon("palette");

            variants.add(ProductVariantDTO.builder()
                    .id("color-01")
                    .name("#01 Đỏ Cam")
                    .subName("Tôn da rạng ngời")
                    .fullLabel("#01 Đỏ Cam (Tôn da - Bán chạy)")
                    .colorHex("#e63946")
                    .priceMultiplier(1.0)
                    .price(basePrice)
                    .badge("Bán chạy")
                    .stock(baseStock)
                    .isDefault(true)
                    .build());
        } else {
            dto.setVariantType("CAPACITY");
            dto.setVariantGroupTitle("Dung tích / Kích thước:");
            dto.setVariantIcon("droplet");

            variants.add(ProductVariantDTO.builder()
                    .id("size-50ml")
                    .name("50ml")
                    .subName("Chuẩn hãng")
                    .fullLabel("50ml (Chuẩn Hãng)")
                    .priceMultiplier(1.0)
                    .price(basePrice)
                    .badge("Bán chạy")
                    .stock(baseStock)
                    .isDefault(true)
                    .build());
        }

        dto.setVariants(variants);
    }

    private double roundPrice(double raw) {
        return Math.round(raw / 1000.0) * 1000.0;
    }
}