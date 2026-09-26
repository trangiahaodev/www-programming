# Task Plan: Quản trị viên thêm mới danh mục (admin-create-category)

## Mục tiêu
Cung cấp màn hình và chức năng cho Quản trị viên (Admin) tạo mới danh mục sản phẩm mỹ phẩm với các trường dữ liệu: Mã danh mục (`categoryCode` - duy nhất), Tên danh mục (`name` - duy nhất), Mô tả (`description`), Trạng thái hoạt động (`active`), kiểm tra tính hợp lệ JSR-380 và ngăn trùng lặp mã/tên danh mục.

## Tiến độ 6 bước ADD (Agent-Driven Development)
- [x] Phase 1: `/create-usecase` (Đặc tả nghiệp vụ)
- [x] Phase 2: `/scaffold` (Sinh code Backend JPA/Controller)
- [x] Phase 3: `/mermaid` (Vẽ sơ đồ)
- [x] Phase 4: `/ux` (Lập bản vẽ UX)
- [x] Phase 5: `/client` (Dựng giao diện Thymeleaf)
- [x] Phase 6: `/supervisor audit-feature` (Thẩm định mã nguồn)

## Quyết định kiến trúc & Đặc tả (Decisions)
- Mã use case: `uc001a-admin-create-category` (nằm trong nhóm quản trị danh mục `uc001`).
- Validation 2 lớp: JSR-380 cho format/rỗng (`CategoryCreateDTO`) + Service logic check trùng mã (`existsByCategoryCode`) và trùng tên (`existsByName`).
- Endpoint: `GET /admin/categories/create` và `POST /admin/categories/create` với PRG pattern (Post-Redirect-Get) và Flash messages.
- Diagram document: [`docs/diagrams/admin-create-category.md`](file:///E:/java-www/www-programming/docs/diagrams/admin-create-category.md).
- Giao diện Thymeleaf: [`category-create.html`](file:///E:/java-www/www-programming/src/main/resources/templates/admin/category-create.html) kế thừa Base Layout, phong cách Minimalism & Subtle Neo-brutalism (8px radius, no shadow, viền phẳng, màu nhấn hồng `#ec4899`, live char counter và validation inline).
- Supervisor Audit: APPROVED (9.9/10).

## Lỗi & Khắc phục (Errors - 3-Strike Rule)
- **Strike Count: 0/3**
- *Lỗi 1 (Đã fix):* Thiếu dependency `spring-boot-starter-validation` trong `pom.xml` dẫn đến lỗi biên dịch `jakarta.validation`. Đã bổ sung dependency và biên dịch thành công 100%.
