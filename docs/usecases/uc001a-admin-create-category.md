# Usecase: Quản trị viên thêm mới danh mục mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                       |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên thêm mới danh mục mỹ phẩm                                                                                                                                                                                                                        |
| **Mã use case**                    | `uc001a-admin-create-category`                                                                                                                                                                                                                                 |
| **Mô tả sơ lược**                  | Quản trị viên (Admin) tạo mới một danh mục mỹ phẩm vào hệ thống gồm các trường: Mã danh mục (`categoryCode`), Tên danh mục (`name`), Mô tả (`description`), và Trạng thái hoạt động (`active`). Hệ thống kiểm tra validation và ngăn trùng mã/tên danh mục. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                          |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                          |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công với quyền `ADMIN`.<br>- Quản trị viên đang ở trang Danh sách danh mục (`/admin/categories`) hoặc thanh điều hướng.                                                                                                    |
| **Hậu điều kiện (Post-condition)** | - Bản ghi `Category` mới được lưu vào bảng `categories` trong CSDL.<br>- Quản trị viên được chuyển hướng về trang danh sách `/admin/categories` kèm thông báo flash thành công.                                                                                |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                                                       | Hệ thống                                                                                                                                                                                             |
| ------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên nhấn nút "Thêm mới danh mục" trên trang danh sách danh mục hoặc menu điều hướng.                                           |                                                                                                                                                                                                      |
|                                                                                                                                             | 2. Hệ thống tiếp nhận yêu cầu (`GET /admin/categories/create`), khởi tạo form danh mục rỗng và hiển thị màn hình tạo mới (`admin/category-create`) với trạng thái mặc định là "Hoạt động" (`active = true`). |
| 3. Quản trị viên nhập thông tin danh mục (Payload gồm: `categoryCode`, `name`, `description`, `active`) và nhấn nút "Lưu danh mục".        |                                                                                                                                                                                                      |
|                                                                                                                                             | 4. Hệ thống tiếp nhận yêu cầu (`POST /admin/categories/create`), kiểm tra tính hợp lệ của dữ liệu đầu vào theo chuẩn JSR-380 (không rỗng, đúng định dạng, giới hạn ký tự).                           |
|                                                                                                                                             | 5. Hệ thống kiểm tra tính duy nhất của Mã danh mục (`categoryCode`) và Tên danh mục (`name`) trong cơ sở dữ liệu.                                                                                    |
|                                                                                                                                             | 6. Hệ thống tạo mới thực thể `Category`, lưu vào cơ sở dữ liệu bảng `categories` và hoàn tất Transaction.                                                                                            |
|                                                                                                                                             | 7. Hệ thống chuyển hướng Quản trị viên về trang danh sách (`GET /admin/categories`) và hiển thị thông báo flash thành công: "Thêm mới danh mục mỹ phẩm thành công!".                                  |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 1.1, 2.1.*

| Actor                                                              | Hệ thống                                                                                                                                        |
| ------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| 3.1. Quản trị viên nhấn nút "Hủy bỏ" / "Quay lại danh sách".       |                                                                                                                                                 |
|                                                                    | 3.2. Hệ thống hủy bỏ thao tác nhập liệu, không lưu dữ liệu và chuyển hướng người dùng về trang danh sách `/admin/categories`.                   |
|                                                                    | 3.3. Kết thúc use case.                                                                                                                         |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 4.1.1, 5.1.1.*

| Actor                                                                                                                                 | Hệ thống                                                                                                                                                                         |
| ------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 2.1.1. Phiên đăng nhập của Quản trị viên hết hạn hoặc không có quyền `ROLE_ADMIN`.                                                    |                                                                                                                                                                                  |
|                                                                                                                                       | 2.1.2. Hệ thống chặn yêu cầu và chuyển hướng về trang đăng nhập `/login` kèm thông báo lỗi "Phiên làm việc đã hết hạn hoặc bạn không có quyền thực hiện thao tác này."           |
|                                                                                                                                       | 2.1.3. Kết thúc use case.                                                                                                                                                        |
| 4.1.1. Dữ liệu nhập vào không hợp lệ vi phạm ràng buộc JSR-380:<br>- `categoryCode` rỗng, chứa khoảng trắng hoặc vượt quá 20 ký tự.<br>- `name` bị rỗng hoặc vượt quá 150 ký tự.<br>- `description` vượt quá 500 ký tự. |                                                                                                                                                                                  |
|                                                                                                                                       | 4.1.2. Hệ thống giữ lại toàn bộ dữ liệu vừa nhập trên form và hiển thị thông báo lỗi inline chi tiết màu đỏ ngay dưới từng trường dữ liệu vi phạm.                              |
|                                                                                                                                       | 4.1.3. Quay lại bước 3 của luồng chính để Quản trị viên điều chỉnh dữ liệu.                                                                                                      |
| 5.1.1. Mã danh mục (`categoryCode`) đã tồn tại trong CSDL (`existsByCategoryCode == true`).                                           |                                                                                                                                                                                  |
|                                                                                                                                       | 5.1.2. Hệ thống giữ lại dữ liệu form và hiển thị thông báo lỗi inline tại trường Mã danh mục: "Mã danh mục '[categoryCode]' đã tồn tại trong hệ thống. Vui lòng chọn mã khác." |
|                                                                                                                                       | 5.1.3. Quay lại bước 3 của luồng chính.                                                                                                                                          |
| 5.2.1. Tên danh mục (`name`) đã tồn tại trong CSDL (`existsByName == true`).                                                         |                                                                                                                                                                                  |
|                                                                                                                                       | 5.2.2. Hệ thống giữ lại dữ liệu form và hiển thị thông báo lỗi inline tại trường Tên danh mục: "Tên danh mục '[name]' đã tồn tại trong hệ thống. Vui lòng đặt tên khác."        |
|                                                                                                                                       | 5.2.3. Quay lại bước 3 của luồng chính.                                                                                                                                          |
| 6.1.1. Xảy ra sự cố kết nối CSDL hoặc lỗi hệ thống trong quá trình lưu dữ liệu.                                                        |                                                                                                                                                                                  |
|                                                                                                                                       | 6.1.2. Hệ thống rollback Transaction, giữ lại dữ liệu form và hiển thị thông báo lỗi tổng thể: "Không thể lưu danh mục vào lúc này. Vui lòng thử lại sau."                       |
|                                                                                                                                       | 6.1.3. Quay lại bước 3 của luồng chính.                                                                                                                                          |

---

## 🛠 Yêu cầu cập nhật Database / Entity / DTO

### 1. DTO Specification (`CategoryCreateDTO.java`)
```java
package iuh.wwwprogramming.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDTO {

    @NotBlank(message = "Mã danh mục không được để trống")
    @Size(min = 2, max = 20, message = "Mã danh mục phải từ 2 đến 20 ký tự")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Mã danh mục chỉ được chứa chữ hoa, chữ số, gạch dưới và gạch ngang (không dấu, không khoảng trắng)")
    private String categoryCode;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 2, max = 150, message = "Tên danh mục phải từ 2 đến 150 ký tự")
    private String name;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;

    @Builder.Default
    private Boolean active = true;
}
```

### 2. Service Layer Method (`CategoryService.java`)
```java
CategoryResponseDTO createCategory(CategoryCreateDTO dto);
```

### 3. Controller Endpoints (`AdminCategoryController.java`)
- `GET /admin/categories/create`: Trả về view form `admin/category-create.html` kèm rỗng `CategoryCreateDTO`.
- `POST /admin/categories/create`: Xử lý submit với `@Valid @ModelAttribute("categoryDTO") CategoryCreateDTO dto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model`.
