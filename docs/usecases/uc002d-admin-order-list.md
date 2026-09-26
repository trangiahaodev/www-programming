# Usecase: Quản trị viên xem danh sách tất cả đơn hàng trực tuyến

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| ---------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên xem danh sách tất cả đơn hàng trực tuyến                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| **Mã use case**                    | `uc002d-admin-order-list`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| **Mô tả sơ lược**                  | Quản trị viên (Admin) truy cập vào khu vực quản trị để xem danh sách toàn bộ các đơn hàng đặt trực tuyến trong hệ thống theo dạng bảng. Dữ liệu hiển thị bao gồm: mã đơn hàng, thông tin khách hàng, ngày đặt hàng, trạng thái đơn hàng, số lượng sản phẩm và tổng tiền. Danh sách mặc định được sắp xếp theo ngày đặt hàng mới nhất trước (`createdAt DESC`). Quản trị viên có thể tìm kiếm, lọc theo trạng thái / khoảng thời gian, phân trang và nhấp chuyển sang xem chi tiết từng đơn hàng (`/admin/orders/{id}`). |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công vào hệ thống với tài khoản có quyền `ROLE_ADMIN`.<br>- Quản trị viên đang ở giao diện Quản trị (Admin Dashboard) hoặc menu điều hướng quản trị.                                                                                                                                                                                                                                                                                                                                           |
| **Hậu điều kiện (Post-condition)** | - Danh sách đơn hàng được hiển thị đầy đủ, chính xác theo điều kiện lọc/tìm kiếm và phân trang.<br>- Danh sách được sắp xếp mặc định theo ngày đặt hàng mới nhất giảm dần.<br>- Thao tác chỉ đọc (Read-only), không làm thay đổi trạng thái hay dữ liệu đơn hàng trong Database.                                                                                                                                                                                                                                               |

---

### Luồng sự kiện chính (Main flow):

| Actor                                                                                            | Hệ thống                                                                                                                                                                                                                                                                                                                                                                                                                        |
| ------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên nhấp chọn menu "Đơn hàng" trên thanh điều hướng quản trị.                       |                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|                                                                                                  | 2. Hệ thống tiếp nhận yêu cầu (`GET /admin/orders`), kiểm tra quyền hạn `ROLE_ADMIN` của phiên làm việc. Hệ thống truy vấn Database lấy danh sách đơn hàng ở trang đầu tiên (mặc định page 0, size 10), sắp xếp theo ngày đặt hàng mới nhất trước (`createdAt DESC`).                                                                                                                                                         |
|                                                                                                  | 3. Hệ thống trả về giao diện danh sách đơn hàng (`admin/order-list`) dưới dạng bảng gồm các cột: Mã đơn hàng (`orderCode`), Thông tin khách hàng (`customerName`, `customerPhone`), Ngày đặt hàng (`orderDate`), Trạng thái đơn hàng (`status`), Số lượng sản phẩm (`totalItems`), Tổng tiền (`totalAmount`), và Cột thao tác (Nút "Xem chi tiết"). Phía trên hiển thị thanh tìm kiếm, bộ lọc trạng thái và phân trang. |
| 4. Quản trị viên nhập từ khóa tìm kiếm (mã đơn hàng, tên khách hàng, SĐT) hoặc chọn trạng thái đơn hàng (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED) hoặc chọn khoảng ngày đặt hàng, sau đó nhấn nút "Tìm kiếm" / "Lọc". |                                                                                                                                                                                                                                                                                                 |
|                                                                                                  | 5. Hệ thống tiếp nhận các tiêu chí tìm kiếm/lọc, validate tính hợp lệ của tham số đầu vào. Hệ thống truy vấn Database lấy danh sách đơn hàng thỏa mãn điều kiện, phân trang tương ứng và cập nhật lại bảng danh sách đơn hàng.                                                                                                                                                                                                  |
| 6. Quản trị viên nhấp vào nút "Xem chi tiết" (icon con mắt hoặc link mã đơn hàng) tại một đơn hàng mong muốn. |                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|                                                                                                  | 7. Hệ thống chuyển hướng Quản trị viên sang trang Xem chi tiết đơn hàng (`GET /admin/orders/{id}`) tương ứng với mã định danh đơn hàng đã chọn.                                                                                                                                                                                                                                                                                |

---

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 4.1, 5.1.*

| Actor                                                                      | Hệ thống                                                                                                                                                                          |
| -------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 4.1. Quản trị viên nhấn nút "Làm mới" / "Đặt lại bộ lọc".                  |                                                                                                                                                                                   |
|                                                                            | 4.2. Hệ thống xóa trắng toàn bộ tiêu chí tìm kiếm và bộ lọc trạng thái, tải lại danh sách toàn bộ đơn hàng ở trang đầu tiên theo thứ tự mới nhất trước.                           |
|                                                                            | 4.3. Quay lại bước 3 của luồng chính.                                                                                                                                             |
| 5.1. Không có đơn hàng nào thỏa mãn điều kiện tìm kiếm hoặc lọc trạng thái. |                                                                                                                                                                                   |
|                                                                            | 5.2. Hệ thống hiển thị thông báo "Không tìm thấy đơn hàng nào phù hợp với điều kiện tìm kiếm" trên bảng dữ liệu. Thanh phân trang được ẩn hoặc đưa về trang 1.                   |
|                                                                            | 5.3. Quay lại bước 4 của luồng chính.                                                                                                                                             |
| 5.4. Quản trị viên nhấp chuyển sang số trang khác trên thanh phân trang hoặc thay đổi kích thước trang (Page Size). |                                                                                                                                                           |
|                                                                            | 5.5. Hệ thống tính toán lại vị trí phân trang, tải dữ liệu danh sách đơn hàng thuộc trang được chọn kèm theo đúng các điều kiện lọc hiện tại và cập nhật lại giao diện hiển thị. |
|                                                                            | 5.6. Quay lại bước 3 của luồng chính.                                                                                                                                             |

---

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 2.1.1, 5.1.1, 7.1.1.*

| Actor                                                                                                   | Hệ thống                                                                                                                                                                                                                     |
| ------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 2.1.1. Phiên đăng nhập của Quản trị viên hết hạn hoặc tài khoản không có quyền truy cập (`ROLE_ADMIN`). |                                                                                                                                                                                                                              |
|                                                                                                         | 2.1.2. Hệ thống từ chối truy cập (HTTP 401/403) và tự động chuyển hướng Quản trị viên về trang đăng nhập `/login` kèm thông báo lỗi: "Phiên làm việc đã hết hạn hoặc bạn không có quyền truy cập chức năng này."            |
|                                                                                                         | 2.1.3. Kết thúc use case.                                                                                                                                                                                                    |
| 2.2.1. Xảy ra lỗi kết nối Database hoặc lỗi máy chủ nội bộ khi tải danh sách đơn hàng.                  |                                                                                                                                                                                                                              |
|                                                                                                         | 2.2.2. Hệ thống ghi log lỗi (SLF4J/Logback) và điều hướng đến trang báo lỗi hệ thống chung (`error/500`) kèm thông báo: "Không thể tải danh sách đơn hàng vào lúc này. Vui lòng thử lại sau."                               |
|                                                                                                         | 2.2.3. Kết thúc use case.                                                                                                                                                                                                    |
| 4.1.1. Bộ lọc khoảng ngày đặt hàng không hợp lệ (ví dụ: ngày bắt đầu lớn hơn ngày kết thúc hoặc định dạng ngày sai). |                                                                                                                                                                                                              |
|                                                                                                         | 4.1.2. Hệ thống phát hiện lỗi validation, giữ nguyên bộ lọc trên form và hiển thị thông báo lỗi cảnh báo: "Khoảng thời gian không hợp lệ: Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc."                                |
|                                                                                                         | 4.1.3. Quay lại bước 4 của luồng chính để Quản trị viên điều chỉnh lại ngày.                                                                                                                                                 |
| 6.1.1. Quản trị viên truy cập hoặc nhấp vào đơn hàng không tồn tại trong hệ thống (ID không hợp lệ hoặc đơn hàng vừa bị xóa vật lý). |                                                                                                                                                                                               |
|                                                                                                         | 6.1.2. Hệ thống ném ngoại lệ `OrderNotFoundException`, giữ người dùng ở lại trang danh sách đơn hàng và hiển thị flash message thông báo lỗi: "Đơn hàng yêu cầu không tồn tại hoặc đã bị gỡ bỏ khỏi hệ thống."           |
|                                                                                                         | 6.1.3. Quay lại bước 3 của luồng chính để hiển thị lại danh sách đơn hàng hiện hành.                                                                                                                                          |

---

## 🛠 Yêu cầu kỹ thuật & Data Payload / Validation Rules

### 1. Data Payload (Dữ liệu trả về cho danh sách đơn hàng)

Dữ liệu trả về cho Thymeleaf View thông qua DTO phẳng (`OrderResponseDTO` / `OrderListDTO`), bao gồm các trường:

| Tên trường (Field) | Kiểu dữ liệu (Data Type) | Ý nghĩa nghiệp vụ | Ví dụ giá trị |
| ------------------ | ------------------------ | ----------------- | ------------- |
| `id` | `String` (UUID 36 chars) | Surrogate Key (Khóa hệ thống dùng cho URL detail) | `"e5b72186-068a-493e-bf6e-ff3a8db29210"` |
| `orderCode` | `String` (max 20 chars) | Business Key hiển thị cho người dùng | `"ORD26000001"` |
| `customerName` | `String` | Họ tên khách hàng / người nhận hàng | `"Nguyễn Thị Lan"` |
| `customerPhone` | `String` | Số điện thoại liên hệ đặt hàng | `"0987654321"` |
| `shippingAddress` | `String` | Địa chỉ nhận hàng giao tận nơi | `"Số 12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM"` |
| `orderDate` | `LocalDateTime` | Thời gian đặt hàng (dùng để sắp xếp mặc định DESC) | `2026-09-24T10:30:00` |
| `status` | `OrderStatus` (Enum) | Trạng thái kỹ thuật: `PENDING`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED` | `PENDING` |
| `statusDisplay` | `String` | Tên hiển thị trạng thái tiếng Việt (badge màu tương ứng) | `"Chờ xác nhận"`, `"Đang giao"`, ... |
| `totalItems` | `Integer` | Tổng số lượng sản phẩm trong đơn | `3` |
| `totalAmount` | `BigDecimal` | Tổng tiền thanh toán của đơn hàng | `850000.00` |
| `paymentMethod` | `String` | Phương thức thanh toán (`COD`, `VNPAY`, `MOMO`, `BANKING`) | `"COD"` |
| `paymentStatus` | `String` | Trạng thái thanh toán (`UNPAID`, `PAID`, `REFUNDED`) | `"UNPAID"` |

**Cấu trúc dữ liệu phân trang (Spring Data `Page<OrderResponseDTO>`):**
- `content`: Danh sách `List<OrderResponseDTO>`.
- `number`: Số thứ tự trang hiện tại (0-indexed).
- `size`: Kích thước mỗi trang (mặc định 10).
- `totalPages`: Tổng số trang tìm thấy.
- `totalElements`: Tổng số đơn hàng thỏa mãn điều kiện.
- `first`: `true` nếu là trang đầu tiên.
- `last`: `true` nếu là trang cuối cùng.

---

### 2. Validation Rules & Quy tắc nghiệp vụ

1. **Quyền truy cập (Access Control):**
   - Bắt buộc kiểm tra role `ADMIN` đối với endpoint `/admin/orders/**`.
   - Nếu chưa xác thực (`Unauthenticated`) -> Chuyển hướng về `/login`.
   - Nếu đã xác thực nhưng thiếu role `ROLE_ADMIN` -> Trả về mã lỗi HTTP 403 Forbidden.

2. **Dữ liệu đơn hàng không tồn tại (Order Not Found):**
   - Khi Admin bấm xem chi tiết với `{id}` không có trong cơ sở dữ liệu: Ném ngoại lệ nghiệp vụ `OrderNotFoundException("Không tìm thấy đơn hàng với mã: " + id)`.
   - Controller bắt ngoại lệ, thêm thông báo cảnh báo `RedirectAttributes.addFlashAttribute("errorMessage", ...)` và điều hướng trở lại `GET /admin/orders`.

3. **Validation ngày đặt hàng (Date Range Validation):**
   - `fromDate` và `toDate` nếu được nhập phải tuân thủ định dạng chuẩn (`yyyy-MM-dd`).
   - Ràng buộc: `fromDate <= toDate`. Nếu `fromDate > toDate`, Controller gắn `BindingResult` lỗi hoặc Service từ chối xử lý, hiển thị lỗi rõ ràng: "Ngày bắt đầu không được lớn hơn ngày kết thúc".
   - `toDate` không được vượt quá thời điểm ngày hiện tại (`<= current date`).

4. **Sắp xếp mặc định (Default Sorting):**
   - Thứ tự hiển thị mặc định: `createdAt DESC` (hoặc `orderDate DESC`) để ưu tiên hiển thị đơn hàng mới đặt nhất lên đầu danh sách.

---

### 3. Database & Entity Mapping (Kiến trúc Dual-Key)

- **Entity `Order` (`orders`):**
  - `@Id @GeneratedValue(strategy = GenerationType.UUID) @Column(length = 36, updatable = false, nullable = false)`: Surrogate Key.
  - `@Column(name = "order_code", unique = true, updatable = false, length = 20, nullable = false)`: Business Key.
  - `@Column(name = "customer_name", nullable = false, length = 150)`
  - `@Column(name = "customer_phone", nullable = false, length = 20)`
  - `@Column(name = "shipping_address", nullable = false, length = 255)`
  - `@Column(name = "total_amount", nullable = false, precision = 12, scale = 2)`
  - `@Enumerated(EnumType.STRING) @Column(name = "status", nullable = false, length = 30)`
  - `@CreationTimestamp @Column(name = "created_at", updatable = false)`
  - `@OneToMany(mappedBy = "order", fetch = FetchType.LAZY) @ToString.Exclude @EqualsAndHashCode.Exclude`: Danh sách `OrderItem`.

- **Entity `OrderItem` (`order_items`):**
  - `@Id @GeneratedValue(strategy = GenerationType.UUID) @Column(length = 36, updatable = false, nullable = false)`: Surrogate Key.
  - `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false) @ToString.Exclude @EqualsAndHashCode.Exclude`
  - `@Column(name = "product_name", nullable = false, length = 150)`
  - `@Column(name = "unit_price", nullable = false, precision = 12, scale = 2)`
  - `@Column(name = "quantity", nullable = false)`
  - `@Column(name = "subtotal", nullable = false, precision = 12, scale = 2)`
