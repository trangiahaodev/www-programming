package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.ProductCreateDTO;
import iuh.wwwprogramming.dto.ProductResponseDTO;
import iuh.wwwprogramming.dto.ProductUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponseDTO createProduct(ProductCreateDTO dto);

    Page<ProductResponseDTO> getProducts(String keyword, String categoryId, Pageable pageable);

    ProductUpdateDTO getProductForEdit(String id);

    ProductResponseDTO updateProduct(String id, ProductUpdateDTO dto);

    void deleteProduct(String id);
}
