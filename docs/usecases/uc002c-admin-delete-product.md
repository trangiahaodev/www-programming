# Usecase: Quản trị viên xóa sản phẩm mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| ---------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên xóa sản phẩm mỹ phẩm                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| **Mã use case**                    | `uc002c-admin-delete-product`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| **Mô tả sơ lược**                  | Quản trị viên (Admin) thực hiện xóa một sản phẩm mỹ phẩm không còn kinh doanh hoặc ngừng phân phối khỏi hệ thống. Luồng xử lý **không tạo trang HTML mới**, hiển thị Hộp thoại xác nhận nguy hiểm (Danger Confirmation Modal) trực tiếp trên trang danh sách sản phẩm. Tầng Service kiểm tra sự tồn tại của sản phẩm và thực hiện xóa trong Transaction. Controller áp dụng mô hình PRG (Post/Redirect/Get) và thông báo kết quả qua Flash message.                                                    |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công với quyền `ADMIN`.<br>- Sản phẩm mỹ phẩm cần xóa đang tồn tại trong cơ sở dữ liệu (`products`) và hiển thị trên giao diện quản trị `/admin/products`.                                                                                                                                                                                                                                                                                                              |
| **Hậu điều kiện (Post-condition)** | - Bản ghi `Product` được xóa hoàn toàn khỏi bảng `products` trong CSDL.<br>- Quản trị viên được chuyển hướng về trang danh sách sản phẩm `/admin/products` kèm thông báo flash thành công (hoặc thông báo lỗi nếu xảy ra ngoại lệ).                                                                                                                                                                                                                                                                       |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                                    | Hệ thống                                                                                                                                                                                                               |
| ------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên nhấn nút "Xóa" (biểu tượng thùng rác màu đỏ `btn-icon-danger`) tại một dòng sản phẩm trên bảng danh sách. |                                                                                                                                                                                                                        |
|                                                                                                                          | 2. Hệ thống hiển thị Hộp thoại xác nhận xóa (Danger Confirmation Modal) chuẩn Neo-Brutalism ngay trên màn hình hiện tại: Hiển thị Tên sản phẩm, Mã sản phẩm (`productCode`), và dòng cảnh báo không thể hoàn tác.     |
| 3. Quản trị viên nhấn nút "Xác nhận xóa" (`btn-danger`) trên hộp thoại.                                                  |                                                                                                                                                                                                                        |
|                                                                                                                          | 4. Hệ thống gửi yêu cầu xóa (`POST /admin/products/delete/{id}`) kèm mã định danh `id` và mã bảo vệ CSRF (`_csrf`).                                                                                                  |
|                                                                                                                          | 5. Tầng Service tìm kiếm thực thể `Product` trong CSDL theo `id`.                                                                                                                                                      |
|                                                                                                                          | 6. Tầng Service thực hiện xóa bản ghi sản phẩm khỏi bảng `products` qua `productRepository.delete(product)` và hoàn tất Transaction.                                                                                   |
|                                                                                                                          | 7. Hệ thống chuyển hướng Quản trị viên về trang danh sách sản phẩm (`GET /admin/products` theo PRG Pattern) và hiển thị thông báo flash thành công: "Xóa sản phẩm mỹ phẩm thành công!".                               |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 3.1.*

| Actor                                                                             | Hệ thống                                                                                                                                 |
| --------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| 3.1. Quản trị viên nhấn nút "Hủy bỏ" hoặc nhấp chuột ra ngoài hộp thoại xác nhận. |                                                                                                                                          |
|                                                                                   | 3.2. Hệ thống đóng hộp thoại xác nhận, không thực hiện bất kỳ thao tác xóa nào và giữ nguyên trạng thái bảng danh sách `/admin/products`. |
|                                                                                   | 3.3. Kết thúc use case.                                                                                                                  |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 5.1.1, 6.1.1.*

| Actor                                                                                        | Hệ thống                                                                                                                                                    |
| -------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 5.1.1. Không tìm thấy sản phẩm theo `id` (Sản phẩm không tồn tại hoặc đã bị xóa trước đó).   |                                                                                                                                                             |
|                                                                                              | 5.1.2. Tầng Service ném `IllegalArgumentException("Không tìm thấy sản phẩm mỹ phẩm cần xóa!")`.                                                             |
|                                                                                              | 5.1.3. Controller bắt ngoại lệ, gán Flash message lỗi `errorMessage` và chuyển hướng về `/admin/products`.                                                  |
|                                                                                              | 5.1.4. Kết thúc use case.                                                                                                                                   |
| 6.1.1. Xảy ra lỗi kết nối CSDL hoặc ràng buộc toàn vẹn bất khả kháng trong quá trình xóa.    |                                                                                                                                                             |
|                                                                                              | 6.1.2. Hệ thống rollback Transaction, gán Flash message lỗi: "Không thể xóa sản phẩm vào lúc này. Vui lòng thử lại sau!" và chuyển hướng về `/admin/products`. |
|                                                                                              | 6.1.3. Kết thúc use case.                                                                                                                                   |

---

## 🛠 Yêu cầu kỹ thuật & Thiết kế API / Service

### 1. Service Layer (`ProductService.java` & `ProductServiceImpl.java`)
```java
public interface ProductService {
    // ... các phương thức hiện có ...
    void deleteProduct(String id);
}
```

**Chi tiết xử lý trong `ProductServiceImpl.java`:**
- Đánh dấu `@Transactional`.
- Tìm kiếm sản phẩm theo `id` qua `productRepository.findById(id)`. Nếu không tìm thấy $\rightarrow$ ném `IllegalArgumentException("Không tìm thấy sản phẩm mỹ phẩm cần xóa!")`.
- Gọi `productRepository.delete(product)` để xóa bản ghi khỏi bảng `products`.

### 2. Controller Layer (`AdminProductController.java`)
```java
@PostMapping("/delete/{id}")
public String deleteProduct(
        @PathVariable("id") String id,
        RedirectAttributes redirectAttributes) {
    try {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm mỹ phẩm thành công!");
    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa sản phẩm vào lúc này. Vui lòng thử lại sau!");
    }
    return "redirect:/admin/products";
}
```

### 3. UI / Thymeleaf UX (`templates/admin/product-list.html`)
- Bổ sung nút "Xóa" (icon thùng rác màu đỏ `btn-icon-danger`) trên từng dòng sản phẩm của bảng dữ liệu.
- Tích hợp Modal xác nhận xóa chuẩn Neo-Brutalism (HTML5 / Vanilla JS, tái sử dụng các class `.modal-backdrop`, `.modal-content`, `.modal-header`, `.modal-body`, `.modal-footer` đã có sẵn trong `admin.css`).
- Form trong Modal submit phương thức `POST` tới URL `@th:action="@{/admin/products/delete/{id}(id=...)}"`.
