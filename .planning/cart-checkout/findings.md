# Findings — Module Giỏ Hàng & Đặt Hàng (Cart & Checkout)

## 1. Cấu trúc CSDL & Ràng buộc Entity
- Đã có entity `Category` (Bảng `categories`):
  - Khóa chính surrogate: `id` (UUID 36 chars)
  - Business key: `category_code` (unique, length 20)
  - `name`, `description`, `active`, timestamps.
- Cần tạo mới `Product` (Bảng `products`):
  - Khóa chính surrogate: `id` (UUID 36 chars)
  - Business key: `product_code` (unique, length 20)
  - `name` (length 150, nullable = false)
  - `price` (BigDecimal, nullable = false)
  - `stock_quantity` (int, default 0, check >= 0)
  - `image_url` (length 500)
  - `description` (length 2000)
  - `active` (boolean, default true)
  - Foreign key: `category_id` (ManyToOne, LAZY, `@ToString.Exclude`, `@EqualsAndHashCode.Exclude`)
- Cần tạo `User` (Bảng `users`):
  - Phục vụ đăng nhập và sở hữu đơn hàng
  - Surrogate key: `id` (UUID 36 chars)
  - Business key: `user_code` (unique, length 20)
  - `email` (unique, nullable = false), `password` (BCrypt), `full_name`, `phone`, `address`, `role` (`ROLE_CUSTOMER`).
- Cần tạo `Order` (Bảng `orders`):
  - Surrogate key: `id` (UUID 36 chars)
  - Business key: `order_code` (unique, length 20, format `DHyyXXXXXX`)
  - `user_id` (ManyToOne User, LAZY)
  - Thông tin nhận hàng: `recipient_name`, `recipient_phone`, `shipping_address`, `note`
  - `total_amount` (BigDecimal)
  - `payment_method` (`COD`, `BANKING`)
  - `status` (`PENDING`, `CONFIRMED`, `SHIPPING`, `COMPLETED`, `CANCELLED`)
- Cần tạo `OrderDetail` (Bảng `order_details`):
  - Surrogate key: `id` (UUID 36 chars)
  - `order_id` (ManyToOne Order, LAZY)
  - `product_id` (ManyToOne Product, LAZY)
  - `unit_price` (BigDecimal)
  - `quantity` (int)
  - `subtotal` (BigDecimal)

## 2. Ràng buộc Giỏ hàng (Session-based Cart)
- Session key: `"CART_SESSION"`
- DTOs:
  - `CartItemDTO`: `productId`, `productCode`, `productName`, `imageUrl`, `unitPrice`, `quantity`, `subtotal`.
  - `CartDTO`: `Map<String, CartItemDTO> items`, `totalQuantity`, `totalAmount`.
- Nghiệp vụ:
  - Thêm sản phẩm: Nếu đã có trong giỏ thì tăng số lượng. Kiểm tra tồn kho trước khi thêm.
  - Cập nhật số lượng: Validate `quantity > 0`, kiểm tra tồn kho. Nếu `quantity <= 0` hoặc ấn xóa thì xóa mục.
  - Xóa sản phẩm: Xóa khỏi map.
  - Làm rỗng giỏ: `clear()`.

## 3. Ràng buộc Đặt hàng (Checkout)
- Bắt buộc đăng nhập (`ROLE_CUSTOMER`). Nếu chưa đăng nhập, redirect tới `/login` với returnUrl về `/checkout`.
- Checkout Form DTO: `CheckoutRequestDTO`
  - `recipientName`: `@NotBlank`, `@Size(max = 100)`
  - `recipientPhone`: `@NotBlank`, `@Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$")`
  - `shippingAddress`: `@NotBlank`, `@Size(max = 255)`
  - `paymentMethod`: `@NotBlank` (COD / BANKING)
  - `note`: optional
- Nghiệp vụ checkout ở Service:
  - Kiểm tra giỏ hàng có rỗng không (nếu rỗng -> ném lỗi).
  - Quét từng sản phẩm trong giỏ: kiểm tra còn hàng không (`product.stockQuantity >= item.quantity`).
  - Trừ số lượng tồn kho `product.setStockQuantity(stock - quantity)`.
  - Tạo `Order` + danh sách `OrderDetail`.
  - Lưu vào Database trong Transaction (`@Transactional`).
  - Xóa giỏ hàng trong Session.

## 4. Dependencies & Lỗi tiềm ẩn
- File `pom.xml` hiện tại chưa có `spring-boot-starter-validation`. Cần bổ sung vào `pom.xml` để kích hoạt các annotation JSR-380 (`@NotBlank`, `@Min`, `@Pattern`, v.v.).
