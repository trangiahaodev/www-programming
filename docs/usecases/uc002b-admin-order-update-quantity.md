# Usecase: Quản trị viên cập nhật số lượng mặt hàng trong đơn hàng trực tuyến

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên cập nhật số lượng mặt hàng trong đơn hàng trực tuyến                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| **Mã use case**                    | `uc002b-admin-order-update-quantity` (`admin-order-update-quantity-b`)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| **Mô tả sơ lược**                  | Quản trị viên (Admin) cập nhật số lượng (`quantity`) của một hoặc nhiều mặt hàng (`OrderItem`) thuộc về một đơn hàng trực tuyến cụ thể. Hệ thống kiểm tra điều kiện hợp lệ của đơn hàng (tồn tại, trạng thái được phép chỉnh sửa), kiểm tra tính hợp lệ của từng mặt hàng (tồn tại, thuộc đúng đơn hàng, số lượng là số nguyên dương hợp lệ), sau đó bắt buộc tự động tính toán lại thành tiền (`subtotal`) của từng mặt hàng và tổng tiền thanh toán (`totalAmount`) của toàn bộ đơn hàng trong một giao dịch duy nhất (`@Transactional`). Tuyệt đối không cho phép cập nhật số lượng mà giữ lại `subtotal` hoặc `totalAmount` cũ. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công vào hệ thống với tài khoản có quyền `ROLE_ADMIN`.<br>- Quản trị viên đang ở màn hình Chi tiết đơn hàng (`/admin/orders/{id}`) hoặc gửi yêu cầu cập nhật qua HTTP POST hợp lệ.<br>- Đơn hàng mục tiêu đang tồn tại trong hệ thống.                                                                                                                                                                                                                                                                                                                                                                 |
| **Hậu điều kiện (Post-condition)** | - Số lượng (`quantity`) và thành tiền (`subtotal = unitPrice * quantity`) của các `OrderItem` được cập nhật chính xác.<br>- Tổng tiền (`totalAmount = sum(subtotal)`) của `Order` được tính lại và cập nhật nhất quán.<br>- Toàn bộ dữ liệu được lưu vết thành công vào Database trong cùng một giao dịch (ACID).<br>- Giao diện hiển thị dữ liệu mới nhất kèm thông báo phản hồi thành công.                                                                                                                                                                                                                                                        |

---

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                                                                              | Hệ thống                                                                                                                                                                                                                                                                                                                                                                             |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1. Quản trị viên điều chỉnh số lượng (`quantity`) của một hoặc nhiều mặt hàng trên giao diện chi tiết đơn hàng và nhấp nút "Cập nhật số lượng" (hoặc "Lưu thay đổi"). |                                                                                                                                                                                                                                                                                                                                                                                      |
|                                                                                                                                                                    | 2. Hệ thống tiếp nhận yêu cầu (`POST /admin/orders/{id}/items` hoặc `POST /admin/orders/update-quantity`), xác thực phiên làm việc và kiểm tra quyền hạn `ROLE_ADMIN`.                                                                                                                                                                                                             |
|                                                                                                                                                                    | 3. Hệ thống kích hoạt kiểm tra tính hợp lệ của dữ liệu đầu vào DTO (`OrderQuantityUpdateRequestDTO`) thông qua cơ chế JSR-380 (`@Valid`): kiểm tra mã đơn hàng không rỗng, danh sách cập nhật không rỗng, từng mã mặt hàng không rỗng, số lượng không null và phải là số nguyên dương $\ge 1$, $\le 999$.                                                                       |
|                                                                                                                                                                    | 4. Tầng Service mở giao dịch (`@Transactional` read-write), truy vấn nạp thông tin đơn hàng `Order` cùng toàn bộ danh sách `OrderItem` liên quan (`findByIdWithItems(orderId)`).                                                                                                                                                                                                   |
|                                                                                                                                                                    | 5. Hệ thống kiểm tra trạng thái đơn hàng: Đơn hàng bắt buộc phải ở trạng thái cho phép chỉnh sửa theo quy định nghiệp vụ (`PENDING` - Chờ xác nhận hoặc `PROCESSING` - Đang xử lý).                                                                                                                                                                                                |
|                                                                                                                                                                    | 6. Hệ thống lặp qua danh sách cập nhật (`itemQuantityUpdates`):<br>a. Xác thực từng `OrderItem` tồn tại trong Database.<br>b. Xác thực từng `OrderItem` thuộc đúng `Order` đang thao tác (`orderItem.getOrder().getId().equals(order.getId())`).<br>c. Cập nhật số lượng mới: `orderItem.setQuantity(newQuantity)`.<br>d. Tính lại thành tiền: `subtotal = unitPrice * quantity`. |
|                                                                                                                                                                    | 7. Hệ thống tính lại tổng giá trị thanh toán của toàn bộ đơn hàng bằng tổng thành tiền của tất cả các mặt hàng thuộc đơn hàng: `totalAmount = sum(all orderItem.subtotal)`.                                                                                                                                                                                                         |
|                                                                                                                                                                    | 8. Hệ thống lưu các thay đổi của `Order` và các `OrderItem` xuống Database và hoàn tất giao dịch (commit transaction).                                                                                                                                                                                                                                                               |
|                                                                                                                                                                    | 9. Hệ thống chuyển hướng Quản trị viên về giao diện Chi tiết đơn hàng (`GET /admin/orders/{id}`) kèm flash message thành công: "Cập nhật số lượng sản phẩm và tính lại tổng tiền đơn hàng thành công."                                                                                                                                                                            |
| 10. Quản trị viên nhìn thấy giao diện hiển thị số lượng mới, thành tiền mới của từng mặt hàng và tổng giá trị thanh toán mới của đơn hàng.                        |                                                                                                                                                                                                                                                                                                                                                                                      |

---

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 1.1, 1.2.*

| Actor                                                                                                           | Hệ thống                                                                                                                                                                             |
| --------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1.1. Quản trị viên chỉ điều chỉnh số lượng của duy nhất một mặt hàng trong đơn hàng.                            |                                                                                                                                                                                      |
|                                                                                                                 | 1.2. Hệ thống tiếp nhận yêu cầu với danh sách chứa 1 phần tử và thực thi tuần tự từ bước 2 đến bước 9 của luồng chính.                                                              |
| 1.3. Quản trị viên thay đổi số lượng nhưng quyết định không lưu và nhấp nút "Hủy thay đổi" / "Đặt lại ban đầu". |                                                                                                                                                                                      |
|                                                                                                                 | 1.4. Giao diện JavaScript/Thymeleaf khôi phục lại giá trị số lượng ban đầu của các mặt hàng trên bảng mà không gửi bất kỳ request nào lên máy chủ. Kết thúc luồng thay thế.        |
| 1.5. Quản trị viên thực hiện cập nhật thông qua lời gọi AJAX / Fetch API không tải lại toàn trang.              |                                                                                                                                                                                      |
|                                                                                                                 | 1.6. Hệ thống thực thi xử lý nghiệp vụ tương tự từ bước 2 đến bước 8, sau đó trả về HTTP 200 OK kèm JSON Payload `OrderDetailResponseDTO`. Giao diện cập nhật lại các chỉ số DOM. |

---

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 2.1.1, 3.1.1, 4.1.1, 5.1.1, 6.1.1, 6.2.1, 7.1.1, 8.1.1.*

| Actor | Hệ thống |
| :--- | :--- |
| **2.1.1.** Phiên đăng nhập của Quản trị viên hết hạn hoặc tài khoản không có quyền truy cập (`ROLE_ADMIN`). | |
| | **2.1.2.** Hệ thống từ chối truy cập (HTTP 401/403) và tự động chuyển hướng Quản trị viên về trang đăng nhập `/login` kèm thông báo lỗi: "Phiên làm việc đã hết hạn hoặc bạn không có quyền truy cập chức năng này." |
| | **2.1.3.** Kết thúc use case. |
| **3.1.1.** Dữ liệu Request vi phạm ràng buộc validation JSR-380 (`orderId` rỗng/sai định dạng UUID, danh sách `itemQuantityUpdates` rỗng/null, `orderItemId` rỗng, hoặc `quantity` null/nhỏ hơn 1/vượt quá 999). | |
| | **3.1.2.** Controller chặn request qua `BindingResult.hasErrors()`, chuyển hướng về trang chi tiết đơn hàng (`/admin/orders/{id}`) kèm flash message cảnh báo: "Dữ liệu cập nhật không hợp lệ. Số lượng sản phẩm phải là số nguyên từ 1 đến 999." |
| | **3.1.3.** Quay lại bước 1 của luồng chính với dữ liệu chưa được cập nhật. |
| **4.1.1.** Mã đơn hàng không tồn tại trong hệ thống (ID không có trong Database). | |
| | **4.1.2.** Tầng Service ném ngoại lệ `OrderNotFoundException`. Controller bắt ngoại lệ, chuyển hướng về trang danh sách đơn hàng (`/admin/orders`) kèm flash message: "Đơn hàng yêu cầu không tồn tại hoặc đã bị xóa khỏi hệ thống." |
| | **4.1.3.** Kết thúc use case. |
| **5.1.1.** Đơn hàng ở trạng thái không được phép chỉnh sửa số lượng (`DELIVERED` - Đã giao hàng, `CANCELLED` - Đã hủy, hoặc `SHIPPED` - Đang giao hàng). | |
| | **5.1.2.** Tầng Service phát hiện vi phạm quy tắc nghiệp vụ, ném ngoại lệ `InvalidOrderStatusException` (hoặc `IllegalStateException`), hủy bỏ toàn bộ thao tác. Controller chuyển hướng về `/admin/orders/{id}` kèm thông báo lỗi: "Đơn hàng ở trạng thái [tên trạng thái] không được phép thay đổi số lượng sản phẩm." |
| | **5.1.3.** Hiển thị lại trang chi tiết đơn hàng kèm thông báo lỗi. |
| **6.1.1.** Một trong các `OrderItem` được yêu cầu cập nhật không tồn tại trong Database (mã `orderItemId` không tìm thấy). | |
| | **6.1.2.** Tầng Service ném ngoại lệ `OrderItemNotFoundException`, tự động rollback toàn bộ giao dịch. Controller chuyển hướng về `/admin/orders/{id}` kèm flash message: "Không tìm thấy mặt hàng cần cập nhật trong hệ thống." |
| | **6.1.3.** Quay lại bước 10 của luồng chính với dữ liệu giữ nguyên như ban đầu. |
| **6.2.1.** Mặt hàng `OrderItem` tồn tại nhưng không thuộc về `Order` đang thao tác (`orderItem.getOrder().getId() != order.getId()`) — Phát hiện dấu hiệu tấn công IDOR hoặc nhầm lẫn dữ liệu. | |
| | **6.2.2.** Tầng Service ném ngoại lệ `InvalidOrderItemException` (hoặc `SecurityException`), rollback toàn bộ giao dịch ngay lập tức và ghi log cảnh báo bảo mật (SLF4J WARN/ERROR). Controller chuyển hướng về `/admin/orders/{id}` kèm thông báo: "Mặt hàng cập nhật không thuộc về đơn hàng này." |
| | **6.2.3.** Quay lại bước 10 của luồng chính. |
| **7.1.1.** Đơn hàng tồn tại nhưng không có bất kỳ mặt hàng con nào (`order.getItems()` null hoặc rỗng). | |
| | **7.1.2.** Tầng Service ném ngoại lệ `IllegalStateException` / `EmptyOrderException`: "Đơn hàng không có sản phẩm nào để cập nhật." Controller chuyển hướng về `/admin/orders/{id}` kèm thông báo lỗi. |
| | **7.1.3.** Quay lại bước 10 của luồng chính. |
| **8.1.1.** Xảy ra sự cố kết nối Database hoặc lỗi `DataAccessException` khi lưu dữ liệu. | |
| | **8.1.2.** Spring Transaction tự động rollback toàn bộ các thay đổi (`quantity`, `subtotal`, `totalAmount`). Hệ thống ghi log chi tiết lỗi hệ thống và chuyển hướng đến trang lỗi (`error/500`) hoặc trang chi tiết kèm flash message: "Không thể lưu cập nhật số lượng vào lúc này. Vui lòng thử lại sau." |
| | **8.1.3.** Kết thúc use case. |

---

## 🛠 Yêu cầu kỹ thuật & Data Payload / Validation Rules

### 1. Data Payload của Request

Dữ liệu từ Form / Client gửi lên Controller thông qua cấu trúc DTO phân cấp (`OrderQuantityUpdateRequestDTO` chứa danh sách `OrderItemQuantityUpdateDTO`):

#### A. DTO yêu cầu cập nhật đơn hàng (`OrderQuantityUpdateRequestDTO`):

| Tên trường (Field) | Kiểu dữ liệu | Ràng buộc JSR-380 | Ý nghĩa nghiệp vụ | Ví dụ giá trị |
| :--- | :--- | :--- | :--- | :--- |
| `orderId` | `String` | `@NotBlank(message = "Mã đơn hàng không được để trống")`<br>`@Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Mã đơn hàng không đúng định dạng UUID")` | Surrogate Key định danh đơn hàng cần cập nhật | `"a3c4db73-b25d-4662-b49e-6b5b696e99fb"` |
| `items` (hoặc `itemQuantityUpdates`) | `List<OrderItemQuantityUpdateDTO>` | `@NotNull(message = "Danh sách cập nhật không được null")`<br>`@NotEmpty(message = "Danh sách cập nhật không được để trống")`<br>`@Valid` | Danh sách các dòng mặt hàng cần cập nhật số lượng mới | *Xem cấu trúc bảng B* |

#### B. DTO chi tiết số lượng mặt hàng (`OrderItemQuantityUpdateDTO`):

| Tên trường (Field) | Kiểu dữ liệu | Ràng buộc JSR-380 | Ý nghĩa nghiệp vụ | Ví dụ giá trị |
| :--- | :--- | :--- | :--- | :--- |
| `orderItemId` | `String` | `@NotBlank(message = "Mã chi tiết đơn hàng không được để trống")`<br>`@Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Mã chi tiết đơn hàng không đúng định dạng UUID")` | Khóa chính của dòng `OrderItem` cần sửa | `"7e91a0c4-1234-5678-abcd-ef0123456789"` |
| `quantity` | `Integer` | `@NotNull(message = "Số lượng không được để trống")`<br>`@Min(value = 1, message = "Số lượng sản phẩm phải lớn hơn hoặc bằng 1")`<br>`@Max(value = 999, message = "Số lượng sản phẩm không được vượt quá 999 món")` | Số lượng mới cần cập nhật | `3` |

---

### 2. Data Payload của Response

Dữ liệu trả về cho View (thông qua Model attribute hoặc JSON response) sử dụng DTO chi tiết hai tầng chuẩn hóa:

#### A. Thông tin đơn hàng sau cập nhật (`OrderDetailResponseDTO`):

| Tên trường (Field) | Kiểu dữ liệu | Ý nghĩa nghiệp vụ |
| :--- | :--- | :--- |
| `id` | `String` (UUID) | Khóa chính đơn hàng |
| `orderCode` | `String` | Mã đơn hàng hiển thị (`ORD26000001`) |
| `customerName` | `String` | Tên người nhận hàng |
| `customerPhone` | `String` | Số điện thoại liên hệ |
| `shippingAddress` | `String` | Địa chỉ giao hàng |
| `note` | `String` | Ghi chú dặn dò |
| `orderDate` | `LocalDateTime` | Thời điểm đặt hàng |
| `updatedAt` | `LocalDateTime` | Thời điểm cập nhật đơn vừa ghi nhận |
| `status` | `OrderStatus` | Trạng thái kỹ thuật của đơn hàng |
| `statusDisplay` | `String` | Tên hiển thị trạng thái tiếng Việt |
| `totalItems` | `Integer` | **Tổng số lượng sản phẩm mới sau cập nhật** |
| `totalAmount` | `BigDecimal` | **Tổng giá trị thanh toán mới sau tính toán lại** |
| `paymentMethod` | `String` | Phương thức thanh toán |
| `paymentStatus` | `String` | Trạng thái thanh toán |
| `items` | `List<OrderItemResponseDTO>` | **Danh sách các mặt hàng với `quantity` và `subtotal` mới** |

#### B. Thông tin chi tiết từng mặt hàng (`OrderItemResponseDTO`):

| Tên trường (Field) | Kiểu dữ liệu | Ý nghĩa nghiệp vụ |
| :--- | :--- | :--- |
| `id` | `String` (UUID) | Khóa chính dòng chi tiết đơn hàng |
| `productCode` | `String` | Mã sản phẩm snapshot |
| `productName` | `String` | Tên sản phẩm snapshot |
| `unitPrice` | `BigDecimal` | Đơn giá snapshot tại thời điểm đặt hàng |
| `quantity` | `Integer` | **Số lượng mới sau cập nhật** |
| `subtotal` | `BigDecimal` | **Thành tiền mới (`unitPrice * quantity`)** |

---

### 3. Quy tắc nghiệp vụ (Business Rules) & Ràng buộc kỹ thuật

| Quy tắc | Ràng buộc nghiệp vụ & Yêu cầu kỹ thuật | Xử lý khi vi phạm |
| :--- | :--- | :--- |
| **BR-01: Phân quyền truy cập** | Endpoint cập nhật `/admin/orders/{id}/items` bắt buộc có vai trò `ROLE_ADMIN`. | Chặn truy cập HTTP 401/403, chuyển hướng `/login`. |
| **BR-02: Kiểm tra tồn tại Order** | `Order` phải tồn tại trong CSDL theo `orderId`. | Ném `OrderNotFoundException`, redirect `/admin/orders` kèm flash error message. |
| **BR-03: Trạng thái đơn được phép sửa** | Chỉ cho phép cập nhật khi đơn hàng có `status` là `PENDING` hoặc `PROCESSING`. Mặc định **CẤM TUYỆT ĐỐI** chỉnh sửa khi đơn ở trạng thái `DELIVERED`, `CANCELLED` hoặc `SHIPPED`. | Ném `InvalidOrderStatusException`, redirect `/admin/orders/{id}` kèm thông báo từ chối. |
| **BR-04: Kiểm tra tồn tại OrderItem** | Từng `orderItemId` trong danh sách gửi lên phải tồn tại trong bảng `order_items`. | Ném `OrderItemNotFoundException`, rollback Transaction toàn bộ. |
| **BR-05: Kiểm tra sở hữu OrderItem (Chống IDOR)** | Từng `OrderItem` bắt buộc phải có `orderItem.getOrder().getId().equals(order.getId())`. Tuyệt đối cấm cập nhật OrderItem của đơn hàng khác. | Ném `InvalidOrderItemException` / `SecurityException`, rollback Transaction, ghi log bảo mật. |
| **BR-06: Ràng buộc số lượng mới** | `quantity` mới không được null, phải là số nguyên dương $\ge 1$ và không vượt quá giới hạn nghiệp vụ $\le 999$. | Chặn bởi JSR-380 `@NotNull`, `@Min(1)`, `@Max(999)` tại Controller và Service validation. |
| **BR-07: Tính lại thành tiền từng món** | Khi `quantity` thay đổi, hệ thống bắt buộc tính lại thành tiền theo công thức: `subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity))`. Tuyệt đối cấm giữ `subtotal` cũ. | Tự động tính toán trong Service Layer trước khi cập nhật entity. |
| **BR-08: Tính lại tổng tiền đơn hàng** | Tổng tiền đơn hàng bắt buộc được tính lại bằng tổng `subtotal` của toàn bộ các mặt hàng: `totalAmount = sum(all orderItem.subtotal)`. Tuyệt đối cấm cập nhật số lượng mà giữ lại `totalAmount` cũ. | Tự động tính toán trong Service Layer trước khi commit transaction. |
| **BR-09: Toàn vẹn Transaction** | Toàn bộ các thao tác: kiểm tra Order, kiểm tra OrderItem, gán quantity, tính subtotal, tính totalAmount và lưu CSDL phải thực thi trong cùng một phương thức có `@Transactional` (rollback on `Exception.class`). | Nếu bất kỳ bước nào lỗi, CSDL được khôi phục 100% về trạng thái ban đầu. |
| **BR-10: Đơn hàng không có mặt hàng** | Đơn hàng phải có ít nhất một mặt hàng con (`order.getItems()` không null và không rỗng). | Ném ngoại lệ, từ chối thực thi cập nhật. |
| **BR-11: Chống Submit trùng lặp** | Sử dụng pattern Post-Redirect-Get (PRG) sau khi cập nhật thành công, kèm Flash message. | Ngăn chặn việc người dùng F5 hoặc gửi lại request nhiều lần. |
