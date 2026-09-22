package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.ProductCardDTO;
import iuh.wwwprogramming.dto.ProductDetailDTO;
import iuh.wwwprogramming.entity.Product;
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

        Page<Product> productPage = productRepository.searchProducts(cleanKeyword, cleanCategory, sortedPageable);
        return productPage.map(this::convertToCardDTO);
    }

    @Override
    public ProductDetailDTO getProductDetail(String idOrCode) {
        if (idOrCode == null || idOrCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hoặc định danh sản phẩm không hợp lệ!");
        }

        Product product = productRepository.findByIdWithCategory(idOrCode.trim())
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
        // Randomize on every page render
        Collections.shuffle(products);

        // Prioritize distinct brands for the showcase Bento grid
        List<Product> selected = new ArrayList<>();
        Set<String> selectedBrands = new HashSet<>();
        for (Product p : products) {
            if (p.getBrand() != null && !p.getBrand().isBlank() && !selectedBrands.contains(p.getBrand().trim())) {
                selected.add(p);
                selectedBrands.add(p.getBrand().trim());
                if (selected.size() >= limit) break;
            }
        }
        // If not enough unique brands, backfill with remaining shuffled products
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

    private Sort determineSort(String sortBy) {
        if (sortBy == null) {
            return Sort.by(Sort.Direction.DESC, "soldCount", "createdAt");
        }
        return switch (sortBy.toLowerCase()) {
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "hot" -> Sort.by(Sort.Direction.DESC, "discount", "soldCount");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "soldCount", "createdAt"); // popular
        };
    }

    private ProductCardDTO convertToCardDTO(Product product) {
        int discount = product.getDiscount() != null ? product.getDiscount() : 0;
        Double price = product.getPrice() != null ? product.getPrice() : 0.0;
        Double originalPrice = null;
        if (discount > 0 && price > 0) {
            originalPrice = (double) Math.round(price / (1.0 - (discount / 100.0)));
        }

        int stock = product.getStock() != null ? product.getStock() : 0;

        return ProductCardDTO.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .brand(product.getBrand())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "")
                .image(product.getImage())
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
        Double price = product.getPrice() != null ? product.getPrice() : 0.0;
        Double originalPrice = null;
        if (discount > 0 && price > 0) {
            originalPrice = (double) Math.round(price / (1.0 - (discount / 100.0)));
        }

        int stock = product.getStock() != null ? product.getStock() : 0;

        ProductDetailDTO dto = ProductDetailDTO.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .brand(product.getBrand())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "")
                .image(product.getImage())
                .price(price)
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

        java.util.List<iuh.wwwprogramming.dto.ProductVariantDTO> variants = new java.util.ArrayList<>();

        if (combined.contains("mặt nạ") || combined.contains("mat na") || combined.contains("mask")) {
            // Nhóm 1: Mặt nạ (Quy cách đóng gói theo miếng, hộp, combo)
            dto.setVariantType("PACK");
            dto.setVariantGroupTitle("Quy cách đóng gói:");
            dto.setVariantIcon("box");

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("piece-1")
                    .name("1 Miếng")
                    .subName("Dùng thử")
                    .fullLabel("1 Miếng (Dùng thử trải nghiệm)")
                    .priceMultiplier(1.0)
                    .price(basePrice)
                    .stock(baseStock)
                    .isDefault(true)
                    .build());

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
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

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("box-10")
                    .name("Hộp 10 miếng")
                    .subName("Tiết kiệm 15%")
                    .fullLabel("Hộp 10 miếng (Tiết kiệm 15%)")
                    .priceMultiplier(8.8)
                    .price(roundPrice(basePrice * 8.8))
                    .badge("Tiết kiệm 15%")
                    .stock(Math.max(3, baseStock / 10))
                    .isDefault(false)
                    .build());

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("combo-20")
                    .name("Combo 2 hộp (20M)")
                    .subName("Siêu tiết kiệm")
                    .fullLabel("Combo 2 hộp (20 miếng - Liệu trình chuyên sâu)")
                    .priceMultiplier(16.5)
                    .price(roundPrice(basePrice * 16.5))
                    .badge("HOT DEAL")
                    .stock(Math.max(2, baseStock / 20))
                    .isDefault(false)
                    .build());

        } else if (combined.contains("son") || combined.contains("lipstick") || combined.contains("tint") || combined.contains("lip")) {
            // Nhóm 2: Son môi (Tone màu thời thượng với swatch màu xinh xắn)
            dto.setVariantType("COLOR");
            dto.setVariantGroupTitle("Tone màu thời thượng:");
            dto.setVariantIcon("palette");

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
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

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("color-02")
                    .name("#02 Hồng Đất")
                    .subName("MLBB Ngọt ngào")
                    .fullLabel("#02 Hồng Đất (MLBB Thời thượng)")
                    .colorHex("#d4757c")
                    .priceMultiplier(1.0)
                    .price(basePrice)
                    .badge("Trendy")
                    .stock(Math.max(10, (int)(baseStock * 0.8)))
                    .isDefault(false)
                    .build());

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("color-03")
                    .name("#03 Đỏ Ruby")
                    .subName("Quyến rũ cổ điển")
                    .fullLabel("#03 Đỏ Ruby (Quyến rũ quý phái)")
                    .colorHex("#9b111e")
                    .priceMultiplier(1.0)
                    .price(basePrice)
                    .stock(Math.max(5, (int)(baseStock * 0.6)))
                    .isDefault(false)
                    .build());

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("color-04")
                    .name("#04 Cam Cháy")
                    .subName("Cá tính ấm áp")
                    .fullLabel("#04 Cam Cháy (Ấm áp nổi bật)")
                    .colorHex("#c04e28")
                    .priceMultiplier(1.0)
                    .price(basePrice)
                    .badge("HOT")
                    .stock(Math.max(8, (int)(baseStock * 0.7)))
                    .isDefault(false)
                    .build());

        } else if (combined.contains("cushion") || combined.contains("phấn") || combined.contains("phan") || combined.contains("kem nền") || combined.contains("foundation") || combined.contains("bb cream")) {
            // Nhóm 3: Trang điểm nền (Tone da & Phấn)
            dto.setVariantType("TONE");
            dto.setVariantGroupTitle("Chọn tone da & Phiên bản:");
            dto.setVariantIcon("palette");

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("tone-21")
                    .name("Tone 21 Sáng")
                    .subName("Da sáng tự nhiên")
                    .fullLabel("Tone 21 - Da Sáng Tự Nhiên (Bán chạy)")
                    .colorHex("#f9e4d4")
                    .priceMultiplier(1.0)
                    .price(basePrice)
                    .badge("Bán chạy")
                    .stock(baseStock)
                    .isDefault(true)
                    .build());

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("tone-23")
                    .name("Tone 23 Tự Nhiên")
                    .subName("Tiệp màu da châu Á")
                    .fullLabel("Tone 23 - Tiệp Da Tự Nhiên")
                    .colorHex("#edd0be")
                    .priceMultiplier(1.0)
                    .price(basePrice)
                    .stock(Math.max(10, (int)(baseStock * 0.8)))
                    .isDefault(false)
                    .build());

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("set-refill")
                    .name("Set + Lõi Refill")
                    .subName("Kèm lõi dự phòng")
                    .fullLabel("Set Hộp + 1 Lõi Refill (Tiết kiệm 20%)")
                    .colorHex("#e8c3ad")
                    .priceMultiplier(1.55)
                    .price(roundPrice(basePrice * 1.55))
                    .badge("Tiết kiệm 20%")
                    .stock(Math.max(5, (int)(baseStock * 0.5)))
                    .isDefault(false)
                    .build());

        } else if (combined.contains("nước hoa") || combined.contains("nuoc hoa") || combined.contains("perfume") || combined.contains("eau de")) {
            // Nhóm 4: Nước hoa (Dung tích & Quy cách)
            dto.setVariantType("CAPACITY");
            dto.setVariantGroupTitle("Dung tích nước hoa:");
            dto.setVariantIcon("droplet");

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("size-10ml")
                    .name("10ml Chiết")
                    .subName("Bỏ túi du lịch")
                    .fullLabel("10ml Chiết (Tiện lợi bỏ túi)")
                    .priceMultiplier(0.28)
                    .price(roundPrice(basePrice * 0.28))
                    .badge("Tiện lợi")
                    .stock(Math.max(15, baseStock))
                    .isDefault(false)
                    .build());

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("size-50ml")
                    .name("50ml")
                    .subName("Chuẩn hãng Fullbox")
                    .fullLabel("50ml (Chuẩn Hãng Fullbox)")
                    .priceMultiplier(1.0)
                    .price(basePrice)
                    .badge("Bán chạy")
                    .stock(baseStock)
                    .isDefault(true)
                    .build());

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("size-100ml")
                    .name("100ml")
                    .subName("Chai lớn tiết kiệm")
                    .fullLabel("100ml (Chai Lớn - Tiết kiệm 30%)")
                    .priceMultiplier(1.68)
                    .price(roundPrice(basePrice * 1.68))
                    .badge("Tiết kiệm 30%")
                    .stock(Math.max(5, (int)(baseStock * 0.6)))
                    .isDefault(false)
                    .build());

        } else if (combined.contains("tẩy trang") || combined.contains("tay trang") || combined.contains("dầu gội") || combined.contains("dau goi") || combined.contains("sữa tắm") || combined.contains("sua tam") || combined.contains("body")) {
            // Nhóm 5: Tẩy trang, Gội xả, Sữa tắm (Dung tích lớn)
            dto.setVariantType("CAPACITY");
            dto.setVariantGroupTitle("Dung tích sản phẩm:");
            dto.setVariantIcon("droplet");

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("size-150ml")
                    .name("150ml")
                    .subName("Bỏ túi tiện lợi")
                    .fullLabel("150ml (Bỏ túi tiện lợi)")
                    .priceMultiplier(0.65)
                    .price(roundPrice(basePrice * 0.65))
                    .stock(Math.max(10, baseStock))
                    .isDefault(false)
                    .build());

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("size-250ml")
                    .name("250ml / 300ml")
                    .subName("Chuẩn hãng")
                    .fullLabel("250ml (Chuẩn Hãng)")
                    .priceMultiplier(1.0)
                    .price(basePrice)
                    .badge("Bán chạy")
                    .stock(baseStock)
                    .isDefault(true)
                    .build());

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("size-500ml")
                    .name("500ml")
                    .subName("Chai lớn tiết kiệm")
                    .fullLabel("500ml (Chai Lớn - Tiết kiệm 25%)")
                    .priceMultiplier(1.6)
                    .price(roundPrice(basePrice * 1.6))
                    .badge("Tiết kiệm 25%")
                    .stock(Math.max(5, (int)(baseStock * 0.7)))
                    .isDefault(false)
                    .build());

        } else if (combined.contains("thiết bị") || combined.contains("thiet bi") || combined.contains("máy") || combined.contains("may") || combined.contains("massage")) {
            // Nhóm 6: Thiết bị làm đẹp (Phiên bản màu sắc)
            dto.setVariantType("DEVICE");
            dto.setVariantGroupTitle("Phiên bản màu sắc:");
            dto.setVariantIcon("sparkles");

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("dev-pink")
                    .name("Hồng Pastel")
                    .subName("Nữ tính ngọt ngào")
                    .fullLabel("Hồng Pastel (Bản Bán Chạy Nhất)")
                    .colorHex("#ffb6c1")
                    .priceMultiplier(1.0)
                    .price(basePrice)
                    .badge("Bán chạy")
                    .stock(baseStock)
                    .isDefault(true)
                    .build());

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("dev-purple")
                    .name("Tím Lilac")
                    .subName("Phiên bản giới hạn")
                    .fullLabel("Tím Lilac (Phiên Bản Giới Hạn)")
                    .colorHex("#c8a2c8")
                    .priceMultiplier(1.0)
                    .price(basePrice)
                    .badge("Trendy")
                    .stock(Math.max(5, (int)(baseStock * 0.6)))
                    .isDefault(false)
                    .build());

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("dev-mint")
                    .name("Xanh Mint")
                    .subName("Tươi mát thanh lịch")
                    .fullLabel("Xanh Mint (Thanh Lịch)")
                    .colorHex("#a8e6cf")
                    .priceMultiplier(1.0)
                    .price(basePrice)
                    .stock(Math.max(8, (int)(baseStock * 0.7)))
                    .isDefault(false)
                    .build());

        } else {
            // Nhóm 7: Skincare thông dụng (Serum, Kem chống nắng, Kem dưỡng, Sữa rửa mặt, Nước hoa hồng)
            dto.setVariantType("CAPACITY");
            dto.setVariantGroupTitle("Dung tích / Kích thước:");
            dto.setVariantIcon("droplet");

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("size-30ml")
                    .name("30ml")
                    .subName("Dùng thử / Du lịch")
                    .fullLabel("30ml (Dùng thử / Du lịch)")
                    .priceMultiplier(0.7)
                    .price(roundPrice(basePrice * 0.7))
                    .stock(Math.max(10, baseStock))
                    .isDefault(false)
                    .build());

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
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

            variants.add(iuh.wwwprogramming.dto.ProductVariantDTO.builder()
                    .id("size-100ml")
                    .name("100ml")
                    .subName("Chai lớn tiết kiệm")
                    .fullLabel("100ml (Chai Lớn - Tiết kiệm 25%)")
                    .priceMultiplier(1.65)
                    .price(roundPrice(basePrice * 1.65))
                    .badge("Tiết kiệm 25%")
                    .stock(Math.max(5, (int)(baseStock * 0.6)))
                    .isDefault(false)
                    .build());
        }

        dto.setVariants(variants);
    }

    private double roundPrice(double raw) {
        return Math.round(raw / 1000.0) * 1000.0;
    }
}
