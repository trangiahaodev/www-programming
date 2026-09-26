# Task Plan: Quản trị viên xóa sản phẩm mỹ phẩm (admin-delete-product)

## Mục tiêu
Cung cấp chức năng cho Quản trị viên (Admin) xóa sản phẩm mỹ phẩm khỏi hệ thống. Yêu cầu hiển thị Hộp thoại xác nhận xóa (Confirmation Modal) theo chuẩn thiết kế Minimalism & Subtle Neo-Brutalism trước khi thực hiện. Kiểm tra tính toàn vẹn dữ liệu (sản phẩm phải tồn tại, xử lý an toàn ràng buộc CSDL), thực hiện xóa trong Transaction và áp dụng PRG Pattern kèm Flash message.

## Tiến độ 6 bước ADD (Agent-Driven Development)
- [x] Phase 1: `/create-usecase` (Đặc tả nghiệp vụ)
- [x] Phase 2: `/scaffold` (Sinh code Backend JPA/Controller)
- [x] Phase 3: `/mermaid` (Vẽ sơ đồ)
- [x] Phase 4: `/ux` (Lập bản vẽ UX)
- [x] Phase 5: `/client` (Dựng giao diện Thymeleaf)
- [x] Phase 6: `/supervisor audit-feature` (Thẩm định mã nguồn)

## Quyết định kiến trúc & Thiết kế (Decisions)
- Mã use case: `uc002c-admin-delete-product` (nhóm `uc002`, `c` = Delete).
- URL Endpoint: `POST /admin/products/delete/{id}` (POST method kèm CSRF Token bảo vệ an toàn chống tấn công CSRF).
- Kiểm tra nghiệp vụ tại Service:
  1. Kiểm tra sự tồn tại của sản phẩm theo `id`. Nếu không tìm thấy $\rightarrow$ ném `IllegalArgumentException("Sản phẩm mỹ phẩm không tồn tại trong hệ thống!")`.
  2. Thực hiện xóa thực thể khỏi bảng `products` qua `productRepository.delete(product)` hoặc `deleteById(id)`.
  3. Hoàn tất Transaction an toàn.
- Giao diện người dùng:
  - Tích hợp **Modal xác nhận xóa (Neo-Brutalism Modal)** trực tiếp trên trang danh sách [`product-list.html`](file:///E:/java-www/www-programming/src/main/resources/templates/admin/product-list.html).
  - Modal hiển thị rõ ràng: Tên sản phẩm, Mã sản phẩm (`productCode`), Cảnh báo hành động không thể hoàn tác, Nút "Hủy bỏ" và Nút "Xác nhận xóa" màu đỏ nguy hiểm (`btn-danger`).
  - Sau khi xóa, chuyển hướng PRG về `GET /admin/products` kèm Flash alert thành công: "Xóa sản phẩm mỹ phẩm thành công!".

## Lỗi & Khắc phục (Errors - 3-Strike Rule)
- **Strike Count: 0/3**
