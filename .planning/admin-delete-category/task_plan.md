# Task Plan: Quản trị viên xóa danh mục (admin-delete-category)

## Mục tiêu
Cung cấp chức năng cho Quản trị viên (Admin) xóa một danh mục mỹ phẩm khỏi hệ thống. Hỗ trợ xác nhận an toàn trước khi xóa (tránh thao tác nhầm lẫn), kiểm tra tính toàn vẹn dữ liệu / ràng buộc nghiệp vụ, xử lý xóa trong cơ sở dữ liệu và thông báo kết quả (thành công hoặc thất bại) về trang danh sách danh mục (`/admin/categories`).

## Tiến độ 6 bước ADD (Agent-Driven Development)
- [x] Phase 1: `/create-usecase` (Đặc tả nghiệp vụ)
- [x] Phase 2: `/scaffold` (Sinh code Backend JPA/Controller)
- [x] Phase 3: `/mermaid` (Vẽ sơ đồ)
- [x] Phase 4: `/ux` (Lập bản vẽ UX)
- [x] Phase 5: `/client` (Dựng giao diện Thymeleaf & Modal xác nhận)
- [x] Phase 6: `/supervisor audit-feature` (Thẩm định mã nguồn)

## Quyết định kiến trúc & Đặc tả (Decisions)
- Mã use case: `uc001c-admin-delete-category` (thuộc bộ chức năng quản lý danh mục mỹ phẩm `uc001`).
- Phương thức HTTP: `POST /admin/categories/{id}/delete` (hoặc method delete qua form POST tuân thủ Spring Security CSRF và PRG pattern).
- An toàn & UX: Yêu cầu xác nhận (Confirmation Modal / Dialog) trước khi thực thi xóa để tránh thao tác vô tình.
- Ràng buộc toàn vẹn: Kiểm tra sự tồn tại của danh mục trước khi xóa, bắt ngoại lệ nghiệp vụ và trả về Flash attribute `errorMessage` nếu xảy ra lỗi.
- Redirect: Sau khi xóa thành công hoặc thất bại, điều hướng về `/admin/categories` kèm thông điệp tương ứng (Flash Message).

## Lỗi & Khắc phục (Errors - 3-Strike Rule)
- **Strike Count: 0/3**
