# Task Plan: Quản trị viên xem danh sách danh mục (admin-view-categories)

## Mục tiêu
Cung cấp màn hình và chức năng cho Quản trị viên (Admin) xem danh sách danh mục mỹ phẩm, hỗ trợ tìm kiếm theo từ khóa (mã hoặc tên danh mục) và phân trang dữ liệu theo chuẩn SSR Thymeleaf + Spring Boot 3.

## Tiến độ 6 bước ADD (Agent-Driven Development)
- [x] Phase 1: `/create-usecase` (Đặc tả nghiệp vụ)
- [x] Phase 2: `/scaffold` (Sinh code Backend JPA/Controller)
- [x] Phase 3: `/mermaid` (Vẽ sơ đồ)
- [x] Phase 4: `/ux` (Lập bản vẽ UX)
- [x] Phase 5: `/client` (Dựng giao diện Thymeleaf)
- [x] Phase 6: `/supervisor audit-feature` (Thẩm định mã nguồn)

## Quyết định kiến trúc & Thiết kế (Decisions)
- **Design System Style:** Chuyển đổi toàn diện sang phong cách **Minimalism & Subtle Neo-Brutalism**.
- **Màu sắc:** Tông chủ đạo Trắng (`#ffffff`), xám nhạt (`#fafafa`), màu nhấn (Accent/Active/Button) là Hồng (`#ec4899` / `#db2777`).
- **Khử bóng đổ (No Shadow):** Loại bỏ hoàn toàn `box-shadow` ở mọi thẻ (card, table, button, input, pagination, badge, sidebar).
- **Hệ thống viền phẳng:** Dùng viền sắc nét `1px solid #e4e4e7` tạo độ tương phản cao, hiện đại.
- **Quy chuẩn bo góc:** Chuẩn hóa `border-radius: 8px` đồng bộ trên toàn bộ component.
- **Kiến trúc dữ liệu:** Duy trì JPQL chuẩn, `countQuery` tối ưu phân trang, transaction `readOnly = true` phân lập an toàn.

## Lỗi & Khắc phục (Errors - 3-Strike Rule)
- **Strike Count: 0/3**
- *Lưu ý kỹ thuật ghi nhận từ Supervisor Audit:* Giá trị `productCount` tạm gán `0L` trong `CategoryServiceImpl` sẽ được chuyển thành JPQL Subquery / Group By khi phát triển phân hệ Sản phẩm (`Product`).
