# Usecase: Khách hàng quản lý giỏ hàng trên Session

| Thành phần                         | Nội dung                                                                                                                                                                                                                                           |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Khách hàng quản lý giỏ hàng trên Session (Shopping Cart Management)                                                                                                                                                                                |
| **Mã use case**                    | `uc002-shopping-cart`                                                                                                                                                                                                                              |
| **Mô tả sơ lược**                  | Cho phép khách hàng (bao gồm cả khách vãng lai `Guest` và khách hàng đã đăng nhập `Customer`) thực hiện xem giỏ hàng, thêm sản phẩm vào giỏ, cập nhật số lượng từng món, xóa món khỏi giỏ hoặc làm rỗng giỏ hàng. Toàn bộ dữ liệu giỏ hàng được lưu trữ tạm thời trong `HttpSession` của người dùng và không lưu vào Database. |
| **Actor chính**                    | Guest (Khách vãng lai), Customer (Khách hàng đã đăng nhập)                                                                                                                                                                                         |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                              |
| **Tiền điều kiện (Pre-condition)** | - Người dùng truy cập website qua trình duyệt có hỗ trợ Cookie/Session.<br>- Các sản phẩm cần mua đang ở trạng thái hoạt động (`active = true`) trong hệ thống.                                                                                     |
| **Hậu điều kiện (Post-condition)** | - Đối tượng `CartDTO` trong `HttpSession` được tạo mới hoặc cập nhật tương ứng.<br>- Số lượng và tổng tiền giỏ hàng hiển thị chính xác theo dữ liệu mới nhất.<br>- Trạng thái Database chưa bị thay đổi.                                       |

---

### Luồng sự kiện chính (Main flow):

| Actor | Hệ thống |
|---|---|
| 1. Người dùng chọn sản phẩm trên trang danh sách hoặc chi tiết sản phẩm, chọn số lượng (Data Payload: `productId`, `quantity`) và nhấn nút "Thêm vào giỏ". | |
| | 2. Hệ thống kiểm tra sản phẩm trong Database theo `productId`, xác nhận sản phẩm tồn tại, đang hoạt động (`active = true`), và số lượng yêu cầu không vượt quá số lượng tồn kho (`product.stockQuantity`). |
| | 3. Hệ thống lấy `CartDTO` từ `HttpSession` (nếu chưa có thì khởi tạo mới). Cập nhật món hàng vào giỏ: nếu đã có thì cộng dồn số lượng, nếu chưa thì thêm mới `CartItemDTO`. |
| | 4. Hệ thống cập nhật lại tổng số lượng (`totalQuantity`) và tổng tiền (`totalAmount`) trong `CartDTO`, lưu lại vào `HttpSession`. |
| | 5. Hệ thống hiển thị thông báo thành công và chuyển hướng (redirect) người dùng về trang giỏ hàng (`GET /cart`) hoặc giữ nguyên trang hiện tại kèm Flash message "Thêm vào giỏ hàng thành công!". |
| 6. Người dùng truy cập trang giỏ hàng (`GET /cart`). | |
| | 7. Hệ thống lấy `CartDTO` từ `HttpSession` và hiển thị giao diện giỏ hàng (`cart/cart-view`) gồm: danh sách sản phẩm, đơn giá, số lượng (input number), thành tiền từng món, tổng tiền đơn hàng, nút "Cập nhật", "Xóa", và nút "Tiến hành thanh toán". |
| 8. Người dùng thay đổi số lượng của một món hàng (Data Payload: `productId`, `quantity`) và nhấn nút cập nhật hoặc đổi số lượng. | |
| | 9. Hệ thống kiểm tra số lượng mới: xác nhận `quantity > 0` và không vượt quá tồn kho hiện tại của sản phẩm. Cập nhật số lượng và tính lại tổng tiền. |
| | 10. Hệ thống lưu `CartDTO` vào Session và tải lại trang giỏ hàng với số liệu mới nhất. |

---

### Luồng sự kiện thay thế (Alternate Flow):

| Actor | Hệ thống |
|---|---|
| 8.1. Người dùng nhấn nút "Xóa" một sản phẩm khỏi giỏ hàng (Data Payload: `productId`). | |
| | 8.2. Hệ thống xóa `CartItemDTO` có mã `productId` tương ứng ra khỏi `CartDTO` trong Session. |
| | 8.3. Hệ thống tính toán lại tổng tiền và cập nhật Session. |
| | 8.4. Quay lại bước 7 của luồng chính. |
| 8.5. Người dùng nhấn nút "Xóa toàn bộ giỏ hàng" (Clear Cart). | |
| | 8.6. Hệ thống làm rỗng toàn bộ các món trong `CartDTO` trong Session. |
| | 8.7. Quay lại bước 7 của luồng chính (hiển thị trạng thái giỏ hàng trống). |
| 7.1. Giỏ hàng trong Session đang trống (`CartDTO.isEmpty() == true`). | |
| | 7.2. Hệ thống hiển thị giao diện Giỏ hàng rỗng (Empty State): icon giỏ hàng trống, dòng thông báo "Giỏ hàng của bạn đang trống!", và nút CTA "Tiếp tục mua sắm" chuyển hướng về `/products`. |

---

### Luồng sự kiện ngoại lệ (Exception Flow):

| Actor | Hệ thống |
|---|---|
| 2.1.1. [Dữ liệu đầu vào vi phạm: `productId` không tồn tại hoặc rỗng] | |
| | 2.1.2. Hệ thống chặn xử lý, thêm Flash attribute lỗi "Sản phẩm không hợp lệ hoặc không tồn tại!". |
| | 2.1.3. Chuyển hướng người dùng về trang danh sách sản phẩm `/products`. |
| 2.2.1. [Dữ liệu đầu vào vi phạm: `quantity` nhỏ hơn hoặc bằng 0 (JSR-380: `@Min(value = 1)`)] | |
| | 2.2.2. Hệ thống hiển thị thông báo lỗi "Số lượng sản phẩm phải lớn hơn 0!". |
| | 2.2.3. Giữ nguyên trạng thái giỏ hàng và quay lại bước 7. |
| 2.3.1. [Số lượng yêu cầu vượt quá tồn kho: `quantity > product.stockQuantity`] | |
| | 2.3.2. Hệ thống chặn cập nhật, hiển thị thông báo lỗi "Số lượng sản phẩm trong kho không đủ (chỉ còn lại [stock] sản phẩm)!". |
| | 2.3.3. Quay lại bước 7 với số lượng cũ của sản phẩm. |

---

## 🛠 Yêu cầu kỹ thuật & Data Contract

### 1. DTO: `CartItemDTO`
- `productId`: String (UUID của sản phẩm)
- `productCode`: String (Mã nghiệp vụ sản phẩm, vd: `SP26000001`)
- `productName`: String (Tên sản phẩm)
- `imageUrl`: String (Ảnh sản phẩm)
- `unitPrice`: BigDecimal (Đơn giá tại thời điểm đưa vào giỏ)
- `quantity`: Integer (Số lượng, `@Min(1)`)
- `subtotal`: BigDecimal (Thành tiền = unitPrice * quantity)

### 2. DTO: `CartDTO`
- `items`: `Map<String, CartItemDTO>` (Key: `productId`)
- `totalQuantity`: Integer (Tổng số lượng món hàng)
- `totalAmount`: BigDecimal (Tổng giá trị giỏ hàng)
- Các hàm tiện ích: `addItem()`, `updateQuantity()`, `removeItem()`, `clear()`, `isEmpty()`.

### 3. Controller Routes
- `GET /cart`: Xem trang giỏ hàng (`cart/cart-view`).
- `POST /cart/add`: Thêm sản phẩm (`productId`, `quantity`).
- `POST /cart/update`: Cập nhật số lượng (`productId`, `quantity`).
- `POST /cart/remove`: Xóa một món (`productId`).
- `POST /cart/clear`: Xóa sạch giỏ hàng.
