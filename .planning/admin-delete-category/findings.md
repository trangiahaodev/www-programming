# Findings & Technical Context: admin-delete-category

## 1. Cấu trúc CSDL & Entity liên quan
- **Bảng:** `categories`
  - `id`: `VARCHAR(36)` (UUID surrogate key, `@Id`)
  - `category_code`: `VARCHAR(20)`, UNIQUE, NOT NULL, `updatable = false` (Business Key bất biến)
  - `name`: `VARCHAR(150)`, NOT NULL
  - `description`: `VARCHAR(500)`
  - `active`: `BIT` / `BOOLEAN`, default `true`, NOT NULL
  - `created_at`: `DATETIME2` (`@CreationTimestamp`, non-updatable)
  - `updated_at`: `DATETIME2` (`@UpdateTimestamp`)

## 2. Quy tắc nghiệp vụ & Kiến trúc xóa danh mục
- **Mã định danh nghiệp vụ:** `uc001c-admin-delete-category`
- **Controller:**
  - Endpoint: `POST /admin/categories/{id}/delete`
  - Tham số: `@PathVariable("id") String id`, `RedirectAttributes redirectAttributes`
  - Logic: Gọi `categoryService.deleteCategory(id)`, gán Flash message thành công (`redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục thành công!")`) hoặc bắt ngoại lệ gán `errorMessage`, sau đó redirect về `/admin/categories`.
- **Service (`CategoryService` & `CategoryServiceImpl`):**
  - Khai báo phương thức: `void deleteCategory(String id);`
  - Kiểm tra danh mục có tồn tại: `Category category = categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục cần xóa!"));`
  - Xóa danh mục qua Repository: `categoryRepository.delete(category);` (hoặc `deleteById(id)`)
  - Quản lý giao dịch: `@Transactional` (write).
- **Giao diện & Tương tác UX:**
  - Nút "Xóa" trên bảng danh sách danh mục [`category-list.html`](file:///E:/java-www/www-programming/src/main/resources/templates/admin/category-list.html) và màn hình chỉnh sửa [`category-update.html`](file:///E:/java-www/www-programming/src/main/resources/templates/admin/category-update.html).
  - Tích hợp Modal xác nhận hành động nguy hiểm (Danger Confirmation Dialog / Modal) hiển thị tên danh mục và cảnh báo không thể hoàn tác.
  - Form submit qua POST kèm CSRF token bảo vệ.
