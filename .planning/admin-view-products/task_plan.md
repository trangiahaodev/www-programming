# Task Plan: Quản trị viên xem danh sách sản phẩm (admin-view-products)

## Mục tiêu
Cung cấp màn hình và chức năng cho Quản trị viên (Admin) xem danh sách toàn bộ sản phẩm mỹ phẩm trong hệ thống. Hỗ trợ tìm kiếm theo từ khóa (mã hoặc tên sản phẩm), lọc theo danh mục mỹ phẩm (`categoryId`), lọc theo trạng thái hoạt động (`active`), và phân trang dữ liệu theo chuẩn SSR Thymeleaf + Spring Boot 3. Truy vấn tối ưu bằng JPQL `JOIN FETCH` để triệt tiêu hoàn toàn vấn đề N+1 Query.

## Tiến độ 6 bước ADD (Agent-Driven Development)
- [x] Phase 1: `/create-usecase` (Đặc tả nghiệp vụ)
- [x] Phase 2: `/scaffold` (Sinh code Backend JPA/Controller)
- [x] Phase 3: `/mermaid` (Vẽ sơ đồ)
- [x] Phase 4: `/ux` (Lập bản vẽ UX)
- [x] Phase 5: `/client` (Dựng giao diện Thymeleaf)
- [x] Phase 6: `/supervisor audit-feature` (Thẩm định mã nguồn)

## Quyết định kiến trúc & Thiết kế (Decisions)
- Mã use case: `uc002d-admin-view-products` (thuộc nhóm quản lý sản phẩm `uc002`, `d` = View/Read).
- Đường dẫn URL: `GET /admin/products` với các tham số: `keyword`, `categoryId`, `page`, `size`.
- Chống N+1 Query: Sử dụng JPQL `JOIN FETCH p.category` và `countQuery` riêng biệt trong `ProductRepository`.
- Mapping dữ liệu: Tầng Service chuyển đổi Entity `Product` sang `ProductResponseDTO` (bao gồm `categoryId` và `categoryName`), gán `readOnly = true` cho transaction.
- Giao diện Thymeleaf: [`product-list.html`](file:///E:/java-www/www-programming/src/main/resources/templates/admin/product-list.html) kế thừa Base Layout `layout/admin-layout.html`, tuân thủ chuẩn Design System **Minimalism & Subtle Neo-brutalism** (8px radius, zero shadow, màu nhấn hồng `#ec4899`, thumbnail preview, status badge, stock badge, pagination controls).

## Lỗi & Khắc phục (Errors - 3-Strike Rule)
- **Strike Count: 0/3**
