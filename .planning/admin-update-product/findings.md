# Findings & Technical Context: admin-update-product

## 1. Cấu trúc CSDL & Quan hệ Entity
- **Entity Product**:
  - `id`: VARCHAR(36) UUID Surrogate Key.
  - `productCode`: VARCHAR(20) Business Key (Unique).
  - `name`: VARCHAR(150) (Unique).
  - `price`: BigDecimal (Min 1,000 VNĐ).
  - `stockQuantity`: Integer (Min 0).
  - `description`: VARCHAR(2000) Nullable.
  - `imageUrl`: VARCHAR(500) Nullable.
  - `active`: Boolean (Default true).
  - `category`: `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "category_id")` -> `Category`.
  - `createdAt`, `updatedAt`: `@CreationTimestamp`, `@UpdateTimestamp`.

## 2. Kiểm tra nghiệp vụ khi Cập nhật (Update Validation)
- Khi Quản trị viên cập nhật:
  1. Sản phẩm với `id` cung cấp phải tồn tại trong CSDL. Nếu không tìm thấy, ném ngoại lệ `EntityNotFoundException` hoặc báo lỗi điều hướng.
  2. Mã sản phẩm (`productCode`): Nếu thay đổi hoặc giữ nguyên, kiểm tra xem có bản ghi khác trùng mã không:
     `boolean existsByProductCodeAndIdNot(String productCode, String id);`
  3. Tên sản phẩm (`name`): Kiểm tra xem có bản ghi khác trùng tên không:
     `boolean existsByNameAndIdNot(String name, String id);`
  4. Danh mục mỹ phẩm (`categoryId`): Phải tồn tại trong CSDL:
     `Category category = categoryRepository.findById(categoryId).orElseThrow(...)`
  5. Cập nhật các trường thông tin và lưu thực thể: `productRepository.save(product)`.
  6. Chuyển hướng theo mô hình PRG (Post/Redirect/Get) về `GET /admin/products` kèm `FlashAttribute("successMessage", "Cập nhật sản phẩm thành công!")`.

## 3. Các thành phần cần triển khai
1. DTO: `ProductUpdateDTO.java` (hoặc tái sử dụng/chỉnh sửa nếu cần, nhưng tốt nhất là tạo riêng `ProductUpdateDTO` với trường `id` và các annotation `@NotBlank`, `@Min`, `@NotNull`).
2. Repository: `ProductRepository.java` bổ sung:
   - `boolean existsByProductCodeAndIdNot(String productCode, String id);`
   - `boolean existsByNameAndIdNot(String name, String id);`
   - Phương thức tìm nạp kèm category: `Optional<Product> findByIdWithCategory(String id);` hoặc `@EntityGraph`.
3. Service: `ProductService.java` & `ProductServiceImpl.java`:
   - `ProductResponseDTO getProductById(String id);`
   - `ProductUpdateDTO getProductForEdit(String id);`
   - `ProductResponseDTO updateProduct(String id, ProductUpdateDTO dto);`
4. Controller: `AdminProductController.java`:
   - `GET /admin/products/edit/{id}`: Hiển thị `product-edit.html`.
   - `POST /admin/products/edit/{id}`: Xử lý cập nhật.
5. View: `templates/admin/product-edit.html`.
