# Findings & Technical Context: admin-update-category

## 1. Cấu trúc CSDL & Entity liên quan
- **Bảng:** `categories`
  - `id`: `VARCHAR(36)` (UUID surrogate key, `@Id`)
  - `category_code`: `VARCHAR(20)`, UNIQUE, NOT NULL, `updatable = false` (Business Key bất biến)
  - `name`: `VARCHAR(150)`, NOT NULL (Index `idx_categories_name`, cần check trùng tên ngoại trừ chính ID hiện tại)
  - `description`: `VARCHAR(500)`
  - `active`: `BIT` / `BOOLEAN`, default `true`, NOT NULL
  - `created_at`: `DATETIME2` (`@CreationTimestamp`, non-updatable)
  - `updated_at`: `DATETIME2` (`@UpdateTimestamp`)

## 2. Ràng buộc DTO & Validation (JSR-380)
- `CategoryUpdateDTO`:
  - `id`: `String` (ID danh mục cần cập nhật, hidden field hoặc path variable)
  - `categoryCode`: `String` (Hiển thị view, readonly/disabled)
  - `name`: `@NotBlank(message = "Tên danh mục không được để trống")`, `@Size(min = 2, max = 150, message = "Tên danh mục phải từ 2 đến 150 ký tự")`
  - `description`: `@Size(max = 500, message = "Mô tả tối đa 500 ký tự")`
  - `active`: `Boolean`

## 3. Quy tắc nghiệp vụ & Kiến trúc
- Controller:
  - `GET /admin/categories/{id}/edit`: Tìm Category theo `id`, map sang `CategoryUpdateDTO` và render view `admin/category-update.html`.
  - `POST /admin/categories/{id}/edit`: Nhận `@Valid @ModelAttribute("categoryDTO") CategoryUpdateDTO dto`, xử lý `BindingResult`, gọi Service cập nhật và chuyển hướng về `/admin/categories` kèm Flash message.
- Service:
  - Tìm Category: `categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục mỹ phẩm cần cập nhật!"));`
  - Kiểm tra trùng tên danh mục ngoại trừ chính nó: `existsByNameAndIdNot(dto.getName(), id)`
  - Cập nhật các trường cho phép sửa (`name`, `description`, `active`).
  - Giao dịch `@Transactional` (write).
- Thymeleaf SSR View: `templates/admin/category-update.html` kế thừa layout `layout/admin-layout.html`, tuân thủ style Minimalism + Neo-brutalism nhẹ (8px radius, no shadow, viền phẳng, màu nhấn Hồng).
