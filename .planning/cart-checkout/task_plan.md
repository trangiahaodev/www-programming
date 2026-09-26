# Task Plan — Module Giỏ Hàng & Thanh Toán (Cart & Checkout)

## Mục tiêu
Phát triển hoàn chỉnh module Giỏ hàng (Session-based Shopping Cart) và Đặt hàng/Thanh toán (Checkout & Order) cho hệ thống bán mỹ phẩm:
- Khách hàng (Guest/Customer) có thể xem giỏ, thêm sản phẩm, cập nhật số lượng, xóa sản phẩm khỏi giỏ trong `HttpSession`.
- Khách hàng bắt buộc đăng nhập tài khoản (`ROLE_CUSTOMER`) mới được tiến hành Đặt hàng (Checkout).
- Tạo thực thể `Product` hoàn chỉnh theo chuẩn Dual-Key.
- Lưu trữ đơn hàng (`Order`, `OrderDetail`) vào Database khi checkout thành công, trừ tồn kho, làm rỗng giỏ hàng.
- Tuân thủ nghiêm ngặt 7 bước theo tài liệu "Quy Trình Phát Triển Tính Năng Chuẩn.pdf".

---

## Tiến độ 6 bước ADD (Agent-Driven Development)
- [x] Phase 1: `/create-usecase` (Đặc tả nghiệp vụ Cart & Checkout tại `docs/usecases/`)
- [x] Phase 2: `/scaffold` (Sinh code Backend JPA/Service/Controller/DTO/Entity)
- [x] Phase 3: `/mermaid` (Vẽ sơ đồ kiến trúc, sequence có rẽ nhánh exception, class diagram)
- [x] Phase 4: `/ux` (Lập bản vẽ UX Blueprint)
- [x] Phase 5: `/client` (Dựng giao diện Thymeleaf cho Cart, Checkout, Order Confirmation)
- [x] Phase 6: `/supervisor audit-feature` (Thẩm định mã nguồn hội đồng, trace luồng dữ liệu)
- [ ] Phase 7: `/commit` (Phân nhóm git add và conventional commit)

---

## Quyết định kiến trúc (Decisions)
1. **Quản lý Giỏ hàng**: Giỏ hàng lưu trữ hoàn toàn trong `HttpSession` (`SESSION_CART`), không ghi xuống Database cho đến khi checkout hoàn tất.
2. **Chiến lược Dual-Key cho Entity**:
   - `Product`: Surrogate Key (`id` UUID 36 ký tự) + Business Key (`productCode` ví dụ: `SP26000001`).
   - `User`: Surrogate Key (`id` UUID 36 ký tự) + Business Key (`userCode` ví dụ: `KH26000001`).
   - `Order`: Surrogate Key (`id` UUID 36 ký tự) + Business Key (`orderCode` ví dụ: `DH26000001`).
   - `OrderDetail`: Surrogate Key (`id` UUID 36 ký tự).
3. **Phân quyền bảo mật**:
   - `/cart/**`: Cho phép Guest và Customer truy cập công khai.
   - `/checkout/**`: Yêu cầu xác thực đăng nhập (`hasRole('CUSTOMER')` hoặc người dùng đã authenticate).
4. **Validation**: Bắt buộc JSR-380 (`@NotNull`, `@Min`, `@NotBlank`) trên tất cả DTOs, Controller kiểm tra bằng `@Valid` và `BindingResult`.
5. **Database Queries**: 100% Spring Data JPA, cấm Native Query và JDBC thuần. Dùng `JOIN FETCH` để tránh lỗi N+1 khi truy vấn Order kèm OrderDetail.

---

## Lỗi & Khắc phục (Errors - 3-Strike Rule)
*(Chưa có lỗi)*
