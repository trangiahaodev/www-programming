# Usecase: Quản trị viên xóa danh mục mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                                                         |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên xóa danh mục mỹ phẩm                                                                                                                                                                                                                                                                                               |
| **Mã use case**                    | `uc001c-admin-delete-category`                                                                                                                                                                                                                                                                                                   |
| **Mô tả sơ lược**                  | Quản trị viên (Admin) thực hiện xóa một danh mục mỹ phẩm không còn sử dụng khỏi hệ thống. Luồng xử lý bắt buộc hiển thị hộp thoại cảnh báo xác nhận trước khi thực hiện xóa. Tầng Service kiểm tra điều kiện nghiệp vụ bằng mã Java và ném ngoại lệ nếu danh mục đang chứa sản phẩm liên kết. Kết quả được thông báo qua Flash message. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                                                                            |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                                                            |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công với quyền `ADMIN`.<br>- Danh mục mỹ phẩm cần xóa đang tồn tại trong hệ thống (ID hợp lệ) và hiển thị trên giao diện quản trị.                                                                                                                                                            |
| **Hậu điều kiện (Post-condition)** | - Bản ghi `Category` được xóa hoàn toàn khỏi bảng `categories` trong cơ sở dữ liệu nếu thỏa mãn điều kiện (không chứa sản phẩm).<br>- Quản trị viên được chuyển hướng về trang danh sách `/admin/categories` kèm thông báo flash (thành công hoặc lỗi nghiệp vụ).                                                                 |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                                    | Hệ thống                                                                                                                                                                                             |
| ------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên nhấn nút "Xóa" (biểu tượng thùng rác màu đỏ) tại danh mục tương ứng trên trang danh sách danh mục.      |                                                                                                                                                                                                      |
|                                                                                                                          | 2. Hệ thống hiển thị hộp thoại / modal cảnh báo xác nhận nguy hiểm (Danger Confirmation Modal): "Bạn có chắc chắn muốn xóa danh mục **[Tên danh mục]** (Mã: [categoryCode])? Hành động này không thể hoàn tác!". |
| 3. Quản trị viên nhấn nút "Xác nhận xóa" trên hộp thoại xác nhận.                                                        |                                                                                                                                                                                                      |
|                                                                                                                          | 4. Hệ thống gửi yêu cầu xóa (`POST /admin/categories/{id}/delete`) kèm mã định danh `id` và CSRF token bảo vệ.                                                                                       |
|                                                                                                                          | 5. Hệ thống tìm kiếm thực thể `Category` trong cơ sở dữ liệu theo `id`.                                                                                                                              |
|                                                                                                                          | 6. Tầng Service kiểm tra điều kiện nghiệp vụ: Xác minh danh mục hiện tại có đang gắn với bất kỳ sản phẩm nào hay không.                                                                              |
|                                                                                                                          | 7. Hệ thống xác nhận danh mục không có sản phẩm liên kết (số lượng sản phẩm = 0), thực hiện xóa bản ghi `Category` khỏi bảng `categories` trong CSDL và hoàn tất Transaction.                       |
|                                                                                                                          | 8. Hệ thống chuyển hướng Quản trị viên về trang danh sách (`GET /admin/categories`) và hiển thị thông báo flash thành công: "Xóa danh mục mỹ phẩm thành công!".                                    |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 3.1.*

| Actor                                                                               | Hệ thống                                                                                                                                   |
| ----------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| 3.1. Quản trị viên nhấn nút "Hủy bỏ" hoặc nhấp chuột ra ngoài hộp thoại xác nhận.   |                                                                                                                                            |
|                                                                                     | 3.2. Hệ thống đóng hộp thoại xác nhận, không thực hiện bất kỳ thao tác xóa nào và giữ nguyên trạng thái trang danh sách `/admin/categories`. |
|                                                                                     | 3.3. Kết thúc use case.                                                                                                                    |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 5.1.1, 6.1.1, 7.1.1.*

| Actor                                                                                                    | Hệ thống                                                                                                                                                                                  |
| -------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 5.1.1. Không tìm thấy danh mục theo `id` (Danh mục không tồn tại hoặc đã bị xóa trước đó).               |                                                                                                                                                                                           |
|                                                                                                          | 5.1.2. Tầng Service ném `IllegalArgumentException("Không tìm thấy danh mục mỹ phẩm cần xóa!")`.                                                                                           |
|                                                                                                          | 5.1.3. Controller bắt ngoại lệ, gán Flash message lỗi `errorMessage` và chuyển hướng về `/admin/categories`.                                                                              |
|                                                                                                          | 5.1.4. Kết thúc use case.                                                                                                                                                                 |
| 6.1.1. Danh mục đang chứa sản phẩm liên kết (Số lượng sản phẩm > 0 vi phạm quy tắc toàn vẹn nghiệp vụ).  |                                                                                                                                                                                           |
|                                                                                                          | 6.1.2. Tầng Service ném `IllegalStateException("Không thể xóa danh mục '[name]' vì đang có sản phẩm thuộc danh mục này! Vui lòng xóa hoặc chuyển sản phẩm sang danh mục khác trước.")`. |
|                                                                                                          | 6.1.3. Controller bắt ngoại lệ, rollback giao dịch, gán Flash message lỗi `errorMessage` và chuyển hướng về `/admin/categories`.                                                             |
|                                                                                                          | 6.1.4. Quản trị viên nhìn thấy thông báo lỗi màu đỏ trên đầu trang danh sách và danh mục vẫn được giữ nguyên vẹn.                                                                        |
|                                                                                                          | 6.1.5. Kết thúc use case.                                                                                                                                                                 |
| 7.1.1. Xảy ra lỗi kết nối CSDL hoặc lỗi hệ thống bất khả kháng trong quá trình xóa.                      |                                                                                                                                                                                           |
|                                                                                                          | 7.1.2. Hệ thống rollback Transaction, gán Flash message lỗi: "Không thể xóa danh mục vào lúc này do lỗi hệ thống. Vui lòng thử lại sau!" và chuyển hướng về `/admin/categories`.            |
|                                                                                                          | 7.1.3. Kết thúc use case.                                                                                                                                                                 |

---

## 🛠 Yêu cầu kỹ thuật & Thiết kế API / Service

### 1. Service Layer (`CategoryService.java` & `CategoryServiceImpl.java`)
```java
public interface CategoryService {
    // ... các phương thức hiện có ...
    void deleteCategory(String id);
}
```

**Chi tiết xử lý trong `CategoryServiceImpl.java`:**
- `@Transactional` (ghi dữ liệu).
- Tìm entity theo `id`, nếu không thấy ném `IllegalArgumentException("Không tìm thấy danh mục mỹ phẩm cần xóa!")`.
- Kiểm tra điều kiện có sản phẩm liên kết (Java code check):
  - *Hiện tại chưa có entity Product, Service chuẩn bị sẵn hook kiểm tra logic (hoặc kiểm tra collection/repository liên quan)*.
  - Nếu có sản phẩm: ném `IllegalStateException("Không thể xóa danh mục '" + category.getName() + "' vì đang có sản phẩm thuộc danh mục này!")`.
- Gọi `categoryRepository.delete(category)` để xóa thực thể.

### 2. Controller Layer (`AdminCategoryController.java`)
```java
@PostMapping("/{id}/delete")
public String deleteCategory(
        @PathVariable("id") String id,
        RedirectAttributes redirectAttributes) {
    try {
        categoryService.deleteCategory(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục mỹ phẩm thành công!");
    } catch (IllegalArgumentException | IllegalStateException e) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa danh mục vào lúc này. Vui lòng thử lại sau!");
    }
    return "redirect:/admin/categories";
}
```

### 3. UI / Thymeleaf UX (`category-list.html`)
- Bổ sung nút "Xóa" (nút icon thùng rác màu đỏ với hiệu ứng hover tinh tế).
- Tích hợp Modal xác nhận xóa chuẩn HTML5 / CSS / Vanilla JS (không dùng popup `alert()` hay `confirm()` mặc định của trình duyệt để đảm bảo giao diện đồng bộ theo phong cách Minimalism & Subtle Neo-brutalism).
- Modal hiển thị:
  - Tiêu đề: **Xác nhận xóa danh mục**
  - Nội dung: Tên và mã danh mục cần xóa kèm dòng cảnh báo nguy hiểm màu đỏ.
  - Nút: "Hủy bỏ" (Secondary Button) và "Xác nhận xóa" (Danger Button - submit Form POST với CSRF).
