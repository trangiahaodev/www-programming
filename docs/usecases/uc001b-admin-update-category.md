# Usecase: Quản trị viên cập nhật thông tin danh mục mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                                       |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên cập nhật thông tin danh mục mỹ phẩm                                                                                                                                                                                                                                                              |
| **Mã use case**                    | `uc001b-admin-update-category`                                                                                                                                                                                                                                                                                 |
| **Mô tả sơ lược**                  | Quản trị viên (Admin) cập nhật thông tin của một danh mục mỹ phẩm hiện có trong hệ thống gồm: Tên danh mục (`name`), Mô tả (`description`), và Trạng thái hoạt động (`active`). Mã danh mục (`categoryCode`) là Business Key bất biến, hiển thị dạng Read-only và không được sửa. Hệ thống kiểm tra JSR-380 và ngăn trùng tên danh mục. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                                                          |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                                          |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công với quyền `ADMIN`.<br>- Danh mục mỹ phẩm cần chỉnh sửa đang tồn tại trong hệ thống (ID hợp lệ).                                                                                                                                                                       |
| **Hậu điều kiện (Post-condition)** | - Bản ghi `Category` được cập nhật thông tin mới trong bảng `categories` CSDL.<br>- Quản trị viên được chuyển hướng về trang danh sách `/admin/categories` kèm thông báo flash thành công.                                                                                                                   |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                                                        | Hệ thống                                                                                                                                                                                                                         |
| -------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên nhấn nút "Chỉnh sửa" (biểu tượng cây bút) tại danh mục tương ứng trên trang danh sách `/admin/categories`.                   |                                                                                                                                                                                                                                  |
|                                                                                                                                              | 2. Hệ thống tiếp nhận yêu cầu (`GET /admin/categories/{id}/edit`), truy vấn dữ liệu danh mục từ CSDL theo `id`, map sang `CategoryUpdateDTO` và hiển thị form chỉnh sửa (`admin/category-update`) với Mã danh mục bị khóa (Read-only / Disabled). |
| 3. Quản trị viên chỉnh sửa các thông tin danh mục (Payload gồm: `name`, `description`, `active`) và nhấn nút "Cập nhật danh mục".             |                                                                                                                                                                                                                                  |
|                                                                                                                                              | 4. Hệ thống tiếp nhận yêu cầu (`POST /admin/categories/{id}/edit`), kiểm tra tính hợp lệ của dữ liệu đầu vào theo chuẩn JSR-380 (Tên không rỗng, giới hạn độ dài ký tự).                                                         |
|                                                                                                                                              | 5. Hệ thống kiểm tra tính duy nhất của Tên danh mục (`name`) đối với các danh mục khác trong cơ sở dữ liệu (`existsByNameAndIdNot`).                                                                                           |
|                                                                                                                                              | 6. Hệ thống cập nhật các trường thay đổi (`name`, `description`, `active`) vào thực thể `Category`, lưu vào cơ sở dữ liệu, tự động cập nhật `updatedAt` và hoàn tất Transaction.                                               |
|                                                                                                                                              | 7. Hệ thống chuyển hướng Quản trị viên về trang danh sách (`GET /admin/categories`) và hiển thị thông báo flash thành công: "Cập nhật danh mục mỹ phẩm thành công!".                                                            |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 1.1, 3.1.*

| Actor                                                               | Hệ thống                                                                                                                                         |
| ------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| 3.1. Quản trị viên nhấn nút "Hủy bỏ" / "Quay lại danh sách".        |                                                                                                                                                  |
|                                                                     | 3.2. Hệ thống hủy bỏ mọi thay đổi vừa nhập, không cập nhật CSDL và chuyển hướng người dùng về trang danh sách `/admin/categories`.             |
|                                                                     | 3.3. Kết thúc use case.                                                                                                                          |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 2.1.1, 4.1.1, 5.1.1.*

| Actor                                                                                                                    | Hệ thống                                                                                                                                                                         |
| ------------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 2.1.1. Không tìm thấy danh mục theo `id` (ID không tồn tại hoặc đã bị xóa).                                              |                                                                                                                                                                                  |
|                                                                                                                          | 2.1.2. Hệ thống chuyển hướng về trang danh sách `/admin/categories` kèm thông báo flash lỗi: "Không tìm thấy danh mục mỹ phẩm cần cập nhật!".                                    |
|                                                                                                                          | 2.1.3. Kết thúc use case.                                                                                                                                                        |
| 4.1.1. Dữ liệu nhập vào không hợp lệ vi phạm ràng buộc JSR-380:<br>- `name` bị rỗng hoặc vượt quá 150 ký tự.<br>- `description` vượt quá 500 ký tự. |                                                                                                                                                                                  |
|                                                                                                                          | 4.1.2. Hệ thống giữ lại toàn bộ dữ liệu vừa nhập trên form và hiển thị thông báo lỗi inline chi tiết màu đỏ ngay dưới từng trường dữ liệu vi phạm.                              |
|                                                                                                                          | 4.1.3. Quay lại bước 3 của luồng chính để Quản trị viên điều chỉnh dữ liệu.                                                                                                      |
| 5.1.1. Tên danh mục (`name`) đã trùng với một danh mục khác trong CSDL (`existsByNameAndIdNot == true`).                  |                                                                                                                                                                                  |
|                                                                                                                          | 5.1.2. Hệ thống giữ lại dữ liệu form và hiển thị thông báo lỗi inline tại trường Tên danh mục: "Tên danh mục '[name]' đã tồn tại trong hệ thống. Vui lòng đặt tên khác."        |
|                                                                                                                          | 5.1.3. Quay lại bước 3 của luồng chính.                                                                                                                                          |
| 6.1.1. Xảy ra sự cố kết nối CSDL hoặc lỗi hệ thống trong quá trình cập nhật.                                             |                                                                                                                                                                                  |
|                                                                                                                          | 6.1.2. Hệ thống rollback Transaction, giữ lại dữ liệu form và hiển thị thông báo lỗi tổng thể: "Không thể cập nhật danh mục vào lúc này. Vui lòng thử lại sau."                    |
|                                                                                                                          | 6.1.3. Quay lại bước 3 của luồng chính.                                                                                                                                          |

---

## 🛠 Yêu cầu cập nhật Database / Entity / DTO

### 1. DTO Specification (`CategoryUpdateDTO.java`)
```java
package iuh.wwwprogramming.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateDTO {

    private String id;

    // Read-only field displaying the immutable Business Key
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

### 2. Repository Layer Method (`CategoryRepository.java`)
```java
boolean existsByNameAndIdNot(String name, String id);
```

### 3. Service Layer Methods (`CategoryService.java`)
```java
CategoryUpdateDTO getCategoryForUpdate(String id);
CategoryResponseDTO updateCategory(String id, CategoryUpdateDTO dto);
```

### 4. Controller Endpoints (`AdminCategoryController.java`)
- `GET /admin/categories/{id}/edit`: Tìm danh mục, map sang `CategoryUpdateDTO`, trả về view form `admin/category-update.html`.
- `POST /admin/categories/{id}/edit`: Xử lý submit cập nhật với `@PathVariable("id") String id, @Valid @ModelAttribute("categoryDTO") CategoryUpdateDTO dto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model`.
