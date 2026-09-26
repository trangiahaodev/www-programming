package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.*;
import iuh.wwwprogramming.dto.ProductDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    Page<ProductCardDTO> searchProducts(String keyword, String categoryId, String sortBy, Pageable pageable);

    ProductDetailDTO getProductDetail(String idOrCode);

    List<ProductCardDTO> getRelatedProducts(String categoryId, String excludeProductId, int limit);

    List<ProductCardDTO> getHotProducts(int limit);

    List<ProductCardDTO> getNewProducts(int limit);

    List<ProductCardDTO> getFeaturedBrandProducts(int limit);

    List<String> getTopBrands();


    ProductResponseDTO createProduct(ProductCreateDTO dto);

    Page<ProductResponseDTO> getProducts(String keyword, String categoryId, Pageable pageable);

    ProductUpdateDTO getProductForEdit(String id);

    ProductResponseDTO updateProduct(String id, ProductUpdateDTO dto);

    void deleteProduct(String id);
}
