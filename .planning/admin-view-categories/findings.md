# Findings & Technical Context: admin-view-categories

## 1. Cấu trúc CSDL & Entity liên quan
- **Bảng:** `categories`
  - `id`: `VARCHAR(36)` (UUID surrogate key, `@GeneratedValue(strategy = GenerationType.UUID)`)
  - `category_code`: `VARCHAR(20)`, UNIQUE, NOT NULL, non-updatable (Business Key, Index `idx_categories_code`)
  - `name`: `VARCHAR(150)`, NOT NULL (Index `idx_categories_name`)
  - `description`: `VARCHAR(500)`
  - `active`: `BIT` / `BOOLEAN`, default `true`, NOT NULL
  - `created_at`: `DATETIME2` (`@CreationTimestamp`, non-updatable)
  - `updated_at`: `DATETIME2` (`@UpdateTimestamp`)

## 2. DTO & Model
- **`CategoryResponseDTO`**:
  - `id` (`String`)
  - `categoryCode` (`String`)
  - `name` (`String`)
  - `description` (`String`)
  - `active` (`Boolean`)
  - `productCount` (`Long`)
  - `createdAt` (`LocalDateTime`)

## 3. Quy ước & Nguyên tắc
- Kiến trúc Controller -> DTO -> Service -> Entity -> Repository.
- Controller: `@RequestMapping("/admin/categories")`, nhận param `keyword`, `page`, `size`, chuyển giao xử lý cho `CategoryService`.
- Repository & Service: Phân trang qua `Pageable`, tìm kiếm không phân biệt hoa thường hoặc theo code/name.
- Thymeleaf SSR: Template `admin/category-list.html` mở rộng từ layout chuẩn.
