# Task Plan: Quản trị viên cập nhật sản phẩm (admin-update-product)

## Mục tiêu
Cung cấp màn hình và chức năng cho Quản trị viên (Admin) cập nhật thông tin sản phẩm mỹ phẩm hiện có trong hệ thống (tên, giá bán, số lượng tồn kho, mô tả, hình ảnh tĩnh, danh mục và trạng thái phát hành). Bảo đảm kiểm tra tính toàn vẹn dữ liệu (JSR-380), kiểm tra trùng lặp mã sản phẩm và tên sản phẩm loại trừ chính nó (Dual-key check), tuân thủ kiến trúc Spring Boot 3 + Spring MVC + Thymeleaf SSR + Microsoft SQL Server.

## Tiến độ 6 bước ADD (Agent-Driven Development)
- [x] Phase 1: `/create-usecase` (Đặc tả nghiệp vụ)
- [x] Phase 2: `/scaffold` (Sinh code Backend JPA/Controller)
- [x] Phase 3: `/mermaid` (Vẽ sơ đồ)
- [x] Phase 4: `/ux` (Lập bản vẽ UX)
- [x] Phase 5: `/client` (Dựng giao diện Thymeleaf)
- [x] Phase 6: `/supervisor audit-feature` (Thẩm định mã nguồn)

## Quyết định kiến trúc & Thiết kế (Decisions)
- Mã use case: `uc002b-admin-update-product` (nhóm sản phẩm `uc002`, `b` = Update).
- URL Endpoints:
  - `GET /admin/products/edit/{id}`: Hiển thị form chỉnh sửa sản phẩm kèm dữ liệu hiện tại và danh sách danh mục hoạt động.
  - `POST /admin/products/edit/{id}`: Tiếp nhận payload cập nhật, xác thực `@Valid @ModelAttribute("productDTO") ProductUpdateDTO`.
- DTO: Tạo `ProductUpdateDTO` chứa các trường cần sửa, bao gồm `id` ẩn hoặc trên path variable.
- Quy tắc kiểm tra trùng lặp (Exclusion Check):
  - Kiểm tra `productCode` trùng với sản phẩm khác: `existsByProductCodeAndIdNot(String productCode, String id)` trong `ProductRepository`.
  - Kiểm tra `name` trùng với sản phẩm khác: `existsByNameAndIdNot(String name, String id)` trong `ProductRepository`.
- Danh mục: Kiểm tra `categoryId` tồn tại và hợp lệ trong CSDL.
- Giao diện Thymeleaf: [`product-edit.html`](file:///E:/java-www/www-programming/src/main/resources/templates/admin/product-edit.html) kế thừa Base Layout `layout/admin-layout.html`, chuẩn Design System **Minimalism & Subtle Neo-brutalism** (8px radius, zero shadow, live image preview, char counter, input addons).

## Lỗi & Khắc phục (Errors - 3-Strike Rule)
- **Strike Count: 0/3**
