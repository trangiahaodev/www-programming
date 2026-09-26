# Findings & Technical Context: admin-create-category

## 1. Cấu trúc CSDL & Entity liên quan
- **Bảng:** `categories`
  - `id`: `VARCHAR(36)` (UUID surrogate key, tự sinh)
  - `category_code`: `VARCHAR(20)`, UNIQUE, NOT NULL, non-updatable (Business Key, Index `idx_categories_code`)
  - `name`: `VARCHAR(150)`, NOT NULL (Index `idx_categories_name`, ràng buộc duy nhất logic ở Service)
  - `description`: `VARCHAR(500)`
  - `active`: `BIT` / `BOOLEAN`, default `true`, NOT NULL
  - `created_at`: `DATETIME2` (`@CreationTimestamp`, non-updatable)
  - `updated_at`: `DATETIME2` (`@UpdateTimestamp`)

## 2. Ràng buộc DTO & Validation (JSR-380)
- `name`: `@NotBlank(message = "Tên danh mục không được để trống")`, `@Size(max = 150, message = "Tên danh mục tối đa 150 ký tự")`
- `description`: `@Size(max = 500, message = "Mô tả tối đa 500 ký tự")`
- `active`: `Boolean` (mặc định `true`)

## 3. Quy tắc nghiệp vụ & Kiến trúc
- Controller: `@GetMapping("/admin/categories/create")` (hiển thị form) và `@PostMapping("/admin/categories/create")` (xử lý submit form kèm `@Valid @ModelAttribute CategoryCreateDTO`).
- Service:
  - Kiểm tra trùng tên danh mục: `if (categoryRepository.existsByName(dto.getName())) throw ...`
  - Sinh mã `categoryCode` tự động.
  - Giao dịch `@Transactional` đảm bảo toàn vẹn.
- Thymeleaf SSR View: `templates/admin/category-create.html` kế thừa layout `layout/admin-layout.html`, tuân thủ style Minimalism + Neo-brutalism nhẹ (8px radius, no shadow, viền phẳng, màu nhấn Hồng).
