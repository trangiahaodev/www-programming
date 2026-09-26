# Usecase: Quản trị viên cập nhật sản phẩm mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| ---------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên cập nhật thông tin sản phẩm mỹ phẩm                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| **Mã use case**                    | `uc002b-admin-update-product`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| **Mô tả sơ lược**                  | Quản trị viên (Admin) cập nhật thông tin của một sản phẩm mỹ phẩm hiện có trong hệ thống bao gồm: Tên sản phẩm, Danh mục mỹ phẩm (`categoryId`), Giá bán (> 0), Số lượng tồn kho ($\ge$ 0), Mô tả chi tiết, Đường dẫn hình ảnh tĩnh (`imageUrl`), và Trạng thái hoạt động (`active`). **Quy tắc sống còn:** Mã sản phẩm (`productCode` - Business Key) cố định và **KHÔNG ĐƯỢC PHÉP CHỈNH SỬA** (vô hiệu hóa trên giao diện và bảo vệ ở tầng Service). Hệ thống kiểm tra JSR-380, kiểm tra trùng tên loại trừ chính nó (`existsByNameAndIdNot`), kiểm tra `categoryId` thực tế trong CSDL và áp dụng PRG Pattern kèm Flash message. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công với quyền `ADMIN`.<br>- Sản phẩm mỹ phẩm cần cập nhật tồn tại trong cơ sở dữ liệu (`products`).<br>- Cơ sở dữ liệu có ít nhất một danh mục mỹ phẩm đang hoạt động.                                                                                                                                                                                                                                                                                                 |
| **Hậu điều kiện (Post-condition)** | - Thông tin sản phẩm được cập nhật thành công vào bảng `products` trong CSDL, thời gian `updated_at` được cập nhật tự động.<br>- Quản trị viên được chuyển hướng về trang danh sách sản phẩm `/admin/products` kèm thông báo flash thành công.                                                                                                                                                                                                                                                             |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                                                                                                                    | Hệ thống                                                                                                                                                                                                                                   |
| -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1. Quản trị viên nhấn nút "Chỉnh sửa" tại một dòng sản phẩm trên bảng danh sách (`GET /admin/products/edit/{id}`).                                                                                      |                                                                                                                                                                                                                                            |
|                                                                                                                                                                                                          | 2. Hệ thống tìm kiếm sản phẩm theo `id` trong CSDL. Nếu tìm thấy, chuyển đổi sang `ProductUpdateDTO`, lấy danh sách danh mục đang hoạt động (`Category`), nạp vào `model` và hiển thị form chỉnh sửa (`admin/product-edit`). Mã sản phẩm `productCode` hiển thị ở chế độ `readonly` / `disabled`. |
| 3. Quản trị viên chỉnh sửa các thông tin cần thiết (`name`, `categoryId`, `price`, `stockQuantity`, `description`, `imageUrl`, `active`) và nhấn nút "Lưu thay đổi".                                     |                                                                                                                                                                                                                                            |
|                                                                                                                                                                                                          | 4. Hệ thống tiếp nhận yêu cầu (`POST /admin/products/edit/{id}`), kiểm tra tính hợp lệ của dữ liệu đầu vào theo chuẩn JSR-380 (`@Valid`).                                                                                                 |
|                                                                                                                                                                                                          | 5. Tầng Service kiểm tra sự tồn tại của sản phẩm gốc theo `id`. Bảo đảm mã sản phẩm `productCode` không bị thay đổi.                                                                                                                      |
|                                                                                                                                                                                                          | 6. Tầng Service kiểm tra trùng tên sản phẩm với các sản phẩm khác trong hệ thống (`existsByNameAndIdNot(name, id)`).                                                                                                                     |
|                                                                                                                                                                                                          | 7. Tầng Service kiểm tra sự tồn tại thực tế của Danh mục trong cơ sở dữ liệu (`categoryRepository.findById(categoryId)`).                                                                                                                 |
|                                                                                                                                                                                                          | 8. Hệ thống cập nhật các trường thông tin của thực thể `Product`, liên kết `Category` mới (nếu có thay đổi), lưu vào cơ sở dữ liệu và hoàn tất Transaction.                                                                              |
|                                                                                                                                                                                                          | 9. Hệ thống chuyển hướng Quản trị viên về trang danh sách sản phẩm (`GET /admin/products` theo PRG Pattern) và hiển thị thông báo flash thành công: "Cập nhật sản phẩm mỹ phẩm thành công!".                                              |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 3.1.*

| Actor                                                         | Hệ thống                                                                                                                                  |
| ------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| 3.1. Quản trị viên nhấn nút "Hủy bỏ" / "Quay lại danh sách".  |                                                                                                                                           |
|                                                               | 3.2. Hệ thống hủy thao tác chỉnh sửa, không lưu thay đổi vào CSDL và chuyển hướng người dùng về trang danh sách sản phẩm `/admin/products`. |
|                                                               | 3.3. Kết thúc use case.                                                                                                                   |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 2.1.1, 4.1.1, 6.1.1.*

| Actor                                                                                                                                                                                                                                                            | Hệ thống                                                                                                                                                                                                                              |
| ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 2.1.1. Sản phẩm không tồn tại trong CSDL (ID không hợp lệ hoặc đã bị xóa bởi phiên làm việc khác).                                                                                                                                                               |                                                                                                                                                                                                                                       |
|                                                                                                                                                                                                                                                                  | 2.1.2. Controller bắt `IllegalArgumentException`, chuyển hướng về trang danh sách `/admin/products` kèm thông báo flash lỗi: "Sản phẩm không tồn tại trong hệ thống!".                                                                 |
|                                                                                                                                                                                                                                                                  | 2.1.3. Kết thúc use case.                                                                                                                                                                                                             |
| 4.1.1. Dữ liệu nhập vào vi phạm ràng buộc JSR-380:<br>- `name` bị rỗng hoặc dưới 2 / trên 150 ký tự.<br>- `categoryId` chưa được chọn.<br>- `price` $\le 0$ hoặc rỗng.<br>- `stockQuantity` $< 0$ hoặc rỗng.<br>- `imageUrl` vượt quá 500 ký tự.<br>- `description` quá 2000 ký tự. |                                                                                                                                                                                                                                       |
|                                                                                                                                                                                                                                                                  | 4.1.2. Hệ thống tải lại form chỉnh sửa, giữ nguyên các dữ liệu đang nhập, tải lại danh mục mỹ phẩm và hiển thị thông báo lỗi inline chi tiết màu đỏ (`th:errors`) ngay dưới từng trường dữ liệu vi phạm.                            |
|                                                                                                                                                                                                                                                                  | 4.1.3. Quay lại bước 3 của luồng chính để Quản trị viên chỉnh sửa.                                                                                                                                                                    |
| 6.1.1. Tên sản phẩm (`name`) bị trùng với một sản phẩm KHÁC đã tồn tại trong CSDL.                                                                                                                                                                              |                                                                                                                                                                                                                                       |
|                                                                                                                                                                                                                                                                  | 6.1.2. Tầng Service ném `IllegalArgumentException("Tên sản phẩm '[name]' đã được sử dụng bởi sản phẩm khác!"). Controller bắt ngoại lệ, đưa thông báo lỗi lên form và hiển thị alert lỗi.                                           |
|                                                                                                                                                                                                                                                                  | 6.1.3. Quay lại bước 3 của luồng chính.                                                                                                                                                                                               |
| 7.1.1. Danh mục được chọn (`categoryId`) không tồn tại trong CSDL.                                                                                                                                                                                               |                                                                                                                                                                                                                                       |
|                                                                                                                                                                                                                                                                  | 7.1.2. Tầng Service ném `IllegalArgumentException("Danh mục mỹ phẩm được chọn không tồn tại trong hệ thống!"). Controller bắt ngoại lệ và hiển thị alert lỗi trên form.                                                                |
|                                                                                                                                                                                                                                                                  | 7.1.3. Quay lại bước 3 của luồng chính.                                                                                                                                                                                               |
| 8.1.1. Lỗi kết nối CSDL hoặc lỗi hệ thống bất khả kháng trong quá trình lưu.                                                                                                                                                                                     |                                                                                                                                                                                                                                       |
|                                                                                                                                                                                                                                                                  | 8.1.2. Hệ thống rollback Transaction, giữ lại dữ liệu form và hiển thị thông báo lỗi: "Không thể cập nhật sản phẩm vào lúc này. Vui lòng thử lại sau!".                                                                               |
|                                                                                                                                                                                                                                                                  | 8.1.3. Quay lại bước 3 của luồng chính.                                                                                                                                                                                               |

---

## 🛠 Yêu cầu kỹ thuật & Thiết kế CSDL / Code

### 1. DTO Specification (`ProductUpdateDTO.java`)
```java
package iuh.wwwprogramming.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {

    private String id;

    // Readonly / Non-updatable field displayed on UI
    private String productCode;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 150, message = "Tên sản phẩm phải từ 2 đến 150 ký tự")
    private String name;

    @NotBlank(message = "Vui lòng chọn danh mục mỹ phẩm")
    private String categoryId;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.01", inclusive = true, message = "Giá bán phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 0")
    private Integer stockQuantity;

    @Size(max = 2000, message = "Mô tả sản phẩm tối đa 2000 ký tự")
    private String description;

    @Size(max = 500, message = "Đường dẫn hình ảnh tối đa 500 ký tự")
    private String imageUrl;

    @Builder.Default
    private Boolean active = true;
}
```

### 2. Repository Layer (`ProductRepository.java`)
Bổ sung các phương thức truy vấn kiểm tra trùng lặp có loại trừ ID hiện tại:
```java
public interface ProductRepository extends JpaRepository<Product, String> {
    
    boolean existsByNameAndIdNot(String name, String id);

    @Query("SELECT p FROM Product p JOIN FETCH p.category c WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") String id);
}
```

### 3. Service Layer (`ProductService.java` & `ProductServiceImpl.java`)
```java
public interface ProductService {
    ProductUpdateDTO getProductForEdit(String id);
    ProductResponseDTO updateProduct(String id, ProductUpdateDTO dto);
}
```

**Quy tắc nghiệp vụ tại tầng Service (`ProductServiceImpl`):**
1. `getProductForEdit(String id)`:
   - Đọc sản phẩm theo `id` (nếu không có $\rightarrow$ ném `IllegalArgumentException("Không tìm thấy sản phẩm!")`).
   - Chuyển đổi sang `ProductUpdateDTO` chứa đầy đủ `id`, `productCode`, `name`, `categoryId`, `price`, `stockQuantity`, `description`, `imageUrl`, `active`.
2. `updateProduct(String id, ProductUpdateDTO dto)`:
   - Đánh dấu `@Transactional`.
   - Tìm kiếm thực thể `Product` hiện tại theo `id`.
   - Kiểm tra `productRepository.existsByNameAndIdNot(cleanName, id)` $\rightarrow$ nếu trùng với sản phẩm khác thì ném `IllegalArgumentException("Tên sản phẩm '...' đã được sử dụng bởi sản phẩm khác!")`.
   - Tìm nạp `Category` từ `categoryRepository.findById(dto.getCategoryId())` $\rightarrow$ nếu không tồn tại thì ném `IllegalArgumentException("Danh mục mỹ phẩm được chọn không tồn tại trong hệ thống!")`.
   - Cập nhật các trường: `name`, `category`, `price`, `stockQuantity`, `description`, `imageUrl`, `active`. **Tuyệt đối KHÔNG thay đổi `productCode`**.
   - Lưu lại `productRepository.save(product)` và trả về `ProductResponseDTO`.

### 4. Controller Layer (`AdminProductController.java`)
- `GET /admin/products/edit/{id}`:
  - Gọi `productService.getProductForEdit(id)`.
  - Nạp `categories` từ `categoryService.getActiveCategories()`.
  - Trả về view `"admin/product-edit"`.
- `POST /admin/products/edit/{id}`:
  - Nhận `@PathVariable("id") String id`, `@Valid @ModelAttribute("productDTO") ProductUpdateDTO dto`, `BindingResult bindingResult`, `RedirectAttributes redirectAttributes`, `Model model`.
  - Nếu `bindingResult.hasErrors()` $\rightarrow$ tải lại danh mục và trả về `"admin/product-edit"`.
  - Gọi `productService.updateProduct(id, dto)`.
  - Gán Flash attribute `successMessage = "Cập nhật sản phẩm mỹ phẩm thành công!"` và chuyển hướng `redirect:/admin/products`.
  - Xử lý ngoại lệ `IllegalArgumentException` $\rightarrow$ nạp `errorMessage` và render lại form.

### 5. View Layer (`templates/admin/product-edit.html`)
- Kế thừa `layout/admin-layout.html`.
- Trường `productCode`: Hiển thị readonly dạng badge/input disabled (giữ nguyên không thể sửa).
- Preview ảnh tĩnh tự động khi thay đổi `imageUrl`.
- Bộ đếm ký tự 2000 ký tự cho mô tả sản phẩm.
- Checkbox kích hoạt trạng thái.
