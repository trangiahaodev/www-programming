package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    List<ProductResponseDTO> getActiveProducts();

    ProductResponseDTO getProductById(String id);

    void initSampleProductsIfEmpty();
}
