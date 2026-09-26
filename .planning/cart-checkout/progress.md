# Progress Log — Module Giỏ Hàng & Thanh Toán (Cart & Checkout)

## [2026-09-23]
- **Brain Setup**:
  - Đã đọc toàn bộ tài liệu hướng dẫn (`AGENTS.md`, `.agents/GUIDE.md`, rules, skills, và quy trình chuẩn từ PDF).
  - Khởi tạo không gian làm việc `.planning/cart-checkout/` gồm: `task_plan.md`, `findings.md`, `progress.md`.
  - Xác nhận yêu cầu từ người dùng: Cần tạo Entity `Product` hoàn chỉnh, và Checkout bắt buộc phải đăng nhập.
- **Phase 1: Đặc tả Use Case**:
  - Tạo thành công `docs/usecases/uc002-shopping-cart.md` cho quản lý giỏ hàng trên Session.
  - Tạo thành công `docs/usecases/uc003-checkout.md` cho luồng đặt hàng và thanh toán.
- **Phase 2: Đổ móng Backend (Scaffold)**:
  - Bổ sung `spring-boot-starter-validation` vào `pom.xml`.
  - Tạo các Entity theo Dual-Key: `Product`, `User`, `Order`, `OrderDetail`, cập nhật `Category`.
  - Tạo các DTOs: `CartItemDTO`, `CartDTO`, `AddToCartRequestDTO`, `UpdateCartItemRequestDTO`, `CheckoutRequestDTO`, `OrderResponseDTO`, `OrderDetailResponseDTO`, `ProductResponseDTO`.
  - Tạo Repositories: `ProductRepository`, `UserRepository`, `OrderRepository` (JOIN FETCH chống N+1), `OrderDetailRepository`.
  - Tạo Services & Impls: `CartService`, `OrderService`, `ProductService`.
  - Tạo Controllers: `CartController`, `CheckoutController`, `ProductController`.
  - Cấu hình bảo mật: `SecurityConfig` và `DataInitializer`.
  - Kiểm thử biên dịch: `mvnw test-compile` BUILD SUCCESS.
- **Phase 3: Sơ đồ Mermaid**:
  - Tạo `docs/diagrams/cart-checkout.md` với 3 sơ đồ (System Architecture, Sequence Diagram rẽ nhánh alt/opt/loop, Class Diagram).
- **Phase 4 & 5: Thiết kế & Code Giao diện (UX/UI & Client)**:
  - Tạo `src/main/resources/static/css/store.css` áp dụng hệ thống token CSS không dùng inline CSS.
  - Tạo `templates/layout/store-layout.html` kế thừa fragment chuẩn cho storefront (Dynamic Cart badge, Security state).
  - Tạo `templates/cart/cart-view.html` hiển thị giỏ hàng, bảng sản phẩm, form cập nhật số lượng và tóm tắt thanh toán.
  - Tạo `templates/checkout/checkout-form.html` binding `@Valid` với thông báo lỗi inline JSR-380.
  - Tạo `templates/checkout/order-success.html` hiển thị biên lai đặt hàng thành công.
  - Tạo `templates/customer/product-list.html` danh mục sản phẩm cho phép thêm nhanh vào giỏ.
