# Findings & Technical Context: admin-view-products

## 1. Cấu trúc CSDL & Entity liên quan
- **Bảng:** `products` (Surrogate Key `id` VARCHAR(36), Business Key `product_code` VARCHAR(20), `name` VARCHAR(150), `price` DECIMAL(18,2), `stock_quantity` INT, `description` VARCHAR(2000), `image_url` VARCHAR(500), `active` BIT, `category_id` VARCHAR(36) FK, `created_at` DATETIME2, `updated_at` DATETIME2).
- **Bảng:** `categories` (`id`, `category_code`, `name`, `active`).

## 2. Truy vấn tối ưu (Chống N+1 Query & Phân trang)
- `ProductRepository`:
  - Cần câu JPQL `searchProducts` hỗ trợ tìm theo từ khóa (`name` hoặc `productCode`), lọc theo `categoryId` (nếu có):
  ```java
  @Query(
      value = """
          SELECT p FROM Product p
          JOIN FETCH p.category c
          WHERE (:keyword IS NULL OR :keyword = ''
                 OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:categoryId IS NULL OR :categoryId = '' OR c.id = :categoryId)
      """,
      countQuery = """
          SELECT COUNT(p) FROM Product p
          WHERE (:keyword IS NULL OR :keyword = ''
                 OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:categoryId IS NULL OR :categoryId = '' OR p.category.id = :categoryId)
      """
  )
  Page<Product> searchProducts(@Param("keyword") String keyword, @Param("categoryId") String categoryId, Pageable pageable);
  ```

## 3. Quy tắc Service & Controller
- **Service (`ProductService` & `ProductServiceImpl`):**
  - Khai báo: `Page<ProductResponseDTO> getProducts(String keyword, String categoryId, Pageable pageable);`
  - Đánh dấu `@Transactional(readOnly = true)`.
  - Sắp xếp mặc định: `Sort.by(Sort.Direction.DESC, "createdAt")`.
- **Controller (`AdminProductController`):**
  - Endpoint: `GET /admin/products`
  - Tham số: `@RequestParam(name = "keyword", required = false) String keyword`, `@RequestParam(name = "categoryId", required = false) String categoryId`, `@RequestParam(name = "page", defaultValue = "0") int page`, `@RequestParam(name = "size", defaultValue = "10") int size`, `Model model`.
  - Model attributes: `products`, `categories` (cho filter dropdown), `currentPage`, `totalPages`, `totalElements`, `size`, `keyword`, `categoryId`.
  - Return view: `"admin/product-list"`.
