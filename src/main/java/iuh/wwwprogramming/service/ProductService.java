package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    // =========================================================================
    // 1. NHÓM HÀM CHO ADMIN (Quản lý CRUD sản phẩm)
    // =========================================================================

    ProductResponseDTO createProduct(ProductCreateDTO dto);

    Page<ProductResponseDTO> getProducts(String keyword, String categoryId, Pageable pageable);

    ProductUpdateDTO getProductForEdit(String id);

    ProductResponseDTO updateProduct(String id, ProductUpdateDTO dto);

    void deleteProduct(String id);


    // =========================================================================
    // 2. NHÓM HÀM CHO CUSTOMER (Giao diện mua sắm)
    // =========================================================================

    Page<ProductCardDTO> searchProducts(String keyword, String categoryId, String sortBy, Pageable pageable);

    ProductDetailDTO getProductDetail(String idOrCode);

    List<ProductCardDTO> getRelatedProducts(String categoryId, String excludeProductId, int limit);

    List<ProductCardDTO> getHotProducts(int limit);

    List<ProductCardDTO> getNewProducts(int limit);

    List<ProductCardDTO> getFeaturedBrandProducts(int limit);

    List<String> getTopBrands();




}
