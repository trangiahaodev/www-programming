# Usecase: Khách hàng tiến hành đặt hàng và thanh toán

| Thành phần                         | Nội dung                                                                                                                                                                                                                                           |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Khách hàng tiến hành đặt hàng và thanh toán (Checkout & Order Placement)                                                                                                                                                                           |
| **Mã use case**                    | `uc003-checkout`                                                                                                                                                                                                                                   |
| **Mô tả sơ lược**                  | Khách hàng đã đăng nhập (`Customer`) thực hiện đặt hàng từ các sản phẩm có trong giỏ hàng (`HttpSession`). Khách hàng cung cấp thông tin người nhận hàng, địa chỉ giao hàng, phương thức thanh toán. Hệ thống kiểm tra hợp lệ, trừ tồn kho, tạo Đơn hàng (`Order` và `OrderDetail`) lưu vào Database, giải phóng giỏ hàng và hiển thị xác nhận. |
| **Actor chính**                    | Customer (Khách hàng đã đăng nhập)                                                                                                                                                                                                                 |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                              |
| **Tiền điều kiện (Pre-condition)** | - Khách hàng đã đăng nhập tài khoản có quyền `ROLE_CUSTOMER`.<br>- Giỏ hàng trong `HttpSession` không được rỗng (`totalQuantity > 0`).<br>- Các sản phẩm trong giỏ hàng vẫn còn tồn tại và đủ số lượng trong kho.                                |
| **Hậu điều kiện (Post-condition)** | - Một bản ghi `Order` và các bản ghi `OrderDetail` tương ứng được lưu thành công vào Database.<br>- Số lượng tồn kho (`stockQuantity`) của từng sản phẩm được trừ tương ứng.<br>- `CartDTO` trong `HttpSession` được dọn sạch (`clear`).<br>- Khách hàng nhận được thông báo đặt hàng thành công và mã đơn hàng (`orderCode`). |

---

### Luồng sự kiện chính (Main flow):

| Actor | Hệ thống |
|---|---|
| 1. Khách hàng nhấn nút "Tiến hành thanh toán" trên trang giỏ hàng (`GET /checkout`). | |
| | 2. Hệ thống kiểm tra phiên đăng nhập (xác nhận người dùng có quyền `ROLE_CUSTOMER`) và kiểm tra giỏ hàng trong Session (xác nhận giỏ không rỗng). |
| | 3. Hệ thống nạp thông tin mặc định của khách hàng (Họ tên, SĐT, Địa chỉ nếu có từ tài khoản `User`) vào `CheckoutRequestDTO` và render trang thanh toán (`checkout/checkout-form`). |
| 4. Khách hàng kiểm tra lại danh sách sản phẩm tóm tắt, nhập/sửa thông tin nhận hàng (Data Payload: `recipientName`, `recipientPhone`, `shippingAddress`, `paymentMethod`, `note`), rồi nhấn nút "Xác nhận đặt hàng". | |
| | 5. Hệ thống tiếp nhận `POST /checkout`, validate dữ liệu đầu vào theo quy chuẩn JSR-380. |
| | 6. Hệ thống thực thi transaction đặt hàng tại Service:<br>a) Kiểm tra tồn kho của từng sản phẩm trong giỏ hàng.<br>b) Giảm trừ `stockQuantity` của từng sản phẩm trong kho.<br>c) Sinh mã đơn hàng nghiệp vụ (`orderCode`, ví dụ `DH26000001`).<br>d) Tạo Entity `Order` và danh sách `OrderDetail`.<br>e) Lưu `Order` và `OrderDetail` vào Database. |
| | 7. Hệ thống làm sạch giỏ hàng trong `HttpSession`. |
| | 8. Hệ thống lưu mã đơn hàng vào Flash Attribute và chuyển hướng (redirect) người dùng đến trang xác nhận thành công (`GET /checkout/success`). |
| | 9. Hệ thống hiển thị trang hoàn tất đơn hàng (`checkout/order-success`) gồm: Mã đơn hàng, Tên người nhận, Tổng thanh toán, Phương thức thanh toán và nút "Tiếp tục mua hàng". |

---

### Luồng sự kiện thay thế (Alternate Flow):

| Actor | Hệ thống |
|---|---|
| 4.1. Khách hàng muốn thay đổi sản phẩm trong giỏ trước khi đặt hàng. | |
| | 4.2. Khách hàng nhấn liên kết "Quay lại giỏ hàng" (`GET /cart`). |
| | 4.3. Hệ thống chuyển hướng về use case `uc002-shopping-cart`. |
| 4.4. Khách hàng chọn phương thức thanh toán Chuyển khoản ngân hàng (`BANKING`). | |
| | 4.5. Hệ thống hiển thị thêm thông tin số tài khoản / mã QR chuyển khoản ngân hàng trên trang xác nhận đơn hàng sau khi hoàn tất đặt hàng. |

---

### Luồng sự kiện ngoại lệ (Exception Flow):

| Actor | Hệ thống |
|---|---|
| 2.1.1. [Người dùng chưa đăng nhập hoặc phiên làm việc hết hạn] | |
| | 2.1.2. Hệ thống lưu lại URL yêu cầu (`/checkout`) vào Session và chuyển hướng đến trang đăng nhập (`/login`). |
| | 2.1.3. Sau khi người dùng đăng nhập thành công với quyền `ROLE_CUSTOMER`, hệ thống tự động đưa người dùng trở lại `/checkout`. |
| 2.2.1. [Giỏ hàng trong Session đang rỗng (`totalQuantity == 0`)] | |
| | 2.2.2. Hệ thống chặn truy cập `/checkout`, thêm thông báo cảnh báo "Giỏ hàng rỗng! Vui lòng chọn sản phẩm trước khi thanh toán." |
| | 2.2.3. Chuyển hướng người dùng về trang danh sách sản phẩm `/products`. |
| 5.1.1. [Dữ liệu đầu vào Form vi phạm luật Validation JSR-380]:<br>- `recipientName` bị để trống (`@NotBlank`) hoặc dài quá 100 ký tự.<br>- `recipientPhone` bị trống hoặc sai định dạng số điện thoại Việt Nam (`@Pattern`).<br>- `shippingAddress` bị để trống (`@NotBlank`).<br>- `paymentMethod` không thuộc danh mục hợp lệ (`COD`, `BANKING`). | |
| | 5.1.2. Hệ thống bắt lỗi qua `BindingResult`, giữ nguyên dữ liệu vừa nhập kèm thông báo lỗi chi tiết dưới từng trường input tương ứng. |
| | 5.1.3. Hiển thị lại giao diện `checkout/checkout-form` để khách hàng chỉnh sửa. |
| 6.1.1. [Số lượng sản phẩm trong kho không đủ đáp ứng]: một hoặc nhiều sản phẩm trong giỏ có `item.quantity > product.stockQuantity`. | |
| | 6.1.2. Hệ thống rollback toàn bộ transaction, ném ra ngoại lệ nghiệp vụ `OutOfStockException` kèm tên sản phẩm và số lượng tồn còn lại. |
| | 6.1.3. Controller bắt lỗi, gán thông báo lỗi vào `errorMessage` và chuyển hướng về trang `/cart` để khách hàng điều chỉnh lại số lượng. |

---

## 🛠 Yêu cầu kỹ thuật & Data Contract

### 1. DTO: `CheckoutRequestDTO`
- `recipientName`: String (`@NotBlank(message = "Họ tên người nhận không được để trống")`, `@Size(max = 100)`)
- `recipientPhone`: String (`@NotBlank(message = "Số điện thoại không được để trống")`, `@Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng")`)
- `shippingAddress`: String (`@NotBlank(message = "Địa chỉ nhận hàng không được để trống")`, `@Size(max = 255)`)
- `paymentMethod`: String (`@NotBlank(message = "Vui lòng chọn phương thức thanh toán")`)
- `note`: String (tối đa 500 ký tự, optional)

### 2. DTO: `OrderResponseDTO`
- `id`: String (Surrogate UUID)
- `orderCode`: String (Business Key, vd `DH26000001`)
- `recipientName`: String
- `recipientPhone`: String
- `shippingAddress`: String
- `paymentMethod`: String
- `totalAmount`: BigDecimal
- `status`: String
- `createdAt`: LocalDateTime
- `items`: `List<OrderItemResponseDTO>`

### 3. Database Entities liên quan
- **`User`**: Đại diện khách hàng đặt hàng (`users`).
- **`Order`**: Đơn hàng (`orders`).
- **`OrderDetail`**: Chi tiết món trong đơn hàng (`order_details`).
- **`Product`**: Sản phẩm được trừ kho (`products`).

### 4. Controller Endpoints
- `GET /checkout`: Hiển thị form đặt hàng.
- `POST /checkout`: Gửi form đặt hàng và xử lý thanh toán.
- `GET /checkout/success`: Trang hoàn tất đơn hàng.
