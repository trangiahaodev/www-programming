# Findings & Technical Context: admin-delete-product

## 1. Cấu trúc CSDL & Ràng buộc Xóa (Deletion Constraints)
- Bảng: `products` (Khóa chính `id` VARCHAR(36) UUID, Khóa nghiệp vụ `product_code` VARCHAR(20) UNIQUE).
- Khóa ngoại: `category_id` trỏ đến `categories(id)`.
- Khi xóa sản phẩm:
  - Chỉ xóa bản ghi trong bảng `products`. Bản ghi `categories` liên quan không bị ảnh hưởng.
  - Phải kiểm tra sản phẩm có tồn tại hay không trước khi thực hiện lệnh xóa.
  - Xử lý bắt ngoại lệ CSDL nếu có lỗi phát sinh (`DataIntegrityViolationException` / `IllegalArgumentException`).

## 2. Các thành phần cần triển khai
1. Repository (`ProductRepository.java`): Sử dụng `deleteById(id)` hoặc `delete(product)` có sẵn trong Spring Data JPA.
2. Service (`ProductService.java` & `ProductServiceImpl.java`):
   - Phương thức: `void deleteProduct(String id);`
   - Đánh dấu `@Transactional`.
   - Tìm kiếm thực thể theo `id`, nếu không có thì ném `IllegalArgumentException`.
   - Gọi `productRepository.delete(product)`.
3. Controller (`AdminProductController.java`):
   - Endpoint: `POST /admin/products/delete/{id}`
   - Tham số: `@PathVariable("id") String id`, `RedirectAttributes redirectAttributes`.
   - Xử lý: Gọi `productService.deleteProduct(id)`, nạp `successMessage` vào Flash attribute, redirect về `GET /admin/products`.
   - Bắt `IllegalArgumentException` / `Exception` $\rightarrow$ gán `errorMessage` vào Flash attribute, redirect về `GET /admin/products`.
4. Giao diện (`templates/admin/product-list.html`):
   - Bổ sung nút bấm thùng rác xóa (`btn-icon-danger`) trên từng dòng sản phẩm.
   - Khi bấm, kích hoạt Modal xác nhận JavaScript (truyền ID, Mã sản phẩm và Tên sản phẩm vào Modal form).
   - Form trong modal submit phương thức POST tới `/admin/products/delete/{id}`.
