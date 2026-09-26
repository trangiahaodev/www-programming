# Task Plan: Quản trị viên cập nhật danh mục (admin-update-category)

## Mục tiêu
Cung cấp màn hình và chức năng cho Quản trị viên (Admin) chỉnh sửa thông tin danh mục mỹ phẩm hiện có: Tên danh mục (`name`), Mô tả (`description`), và Trạng thái hoạt động (`active`). Lưu ý: Mã danh mục (`categoryCode`) là Business Key bất biến (`updatable = false`) hiển thị dạng Read-only / Disabled. Hệ thống kiểm tra tính hợp lệ JSR-380 và ngăn trùng lặp tên danh mục với các danh mục khác trong cơ sở dữ liệu.

## Tiến độ 6 bước ADD (Agent-Driven Development)
- [x] Phase 1: `/create-usecase` (Đặc tả nghiệp vụ)
- [x] Phase 2: `/scaffold` (Sinh code Backend JPA/Controller)
- [x] Phase 3: `/mermaid` (Vẽ sơ đồ)
- [x] Phase 4: `/ux` (Lập bản vẽ UX)
- [x] Phase 5: `/client` (Dựng giao diện Thymeleaf)
- [ ] Phase 6: `/supervisor audit-feature` (Thẩm định mã nguồn)

## Quyết định kiến trúc & Đặc tả (Decisions)
- Mã use case: `uc001b-admin-update-category` (nằm trong nhóm quản trị danh mục `uc001`).
- Bất biến Business Key: Trường `categoryCode` chỉ đọc (Read-only / Disabled), không cho phép chỉnh sửa.
- Validation 2 lớp: JSR-380 (`CategoryUpdateDTO`) + Service logic check trùng tên loại trừ chính ID đang sửa (`existsByNameAndIdNot`).
- Endpoint: `GET /admin/categories/{id}/edit` và `POST /admin/categories/{id}/edit` với PRG pattern (Post-Redirect-Get) và Flash messages.
- Diagram document: [`docs/diagrams/admin-update-category.md`](file:///E:/java-www/www-programming/docs/diagrams/admin-update-category.md).
- Giao diện Thymeleaf: [`category-update.html`](file:///E:/java-www/www-programming/src/main/resources/templates/admin/category-update.html) kế thừa Base Layout, phong cách Minimalism & Subtle Neo-brutalism (8px radius, no shadow, viền phẳng, màu nhấn hồng `#ec4899`, readonly lock icon, live char counter và validation inline).

## Lỗi & Khắc phục (Errors - 3-Strike Rule)
- **Strike Count: 0/3**
