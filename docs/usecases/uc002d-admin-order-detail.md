# Usecase: Quản trị viên xem chi tiết đơn hàng

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                                  |
| ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên xem chi tiết đơn hàng                                                                                                                                                                                                                                                                       |
| **Mã use case**                    | `uc002d-admin-order-detail`                                                                                                                                                                                                                                                                               |
| **Mô tả sơ lược**                  | Quản trị viên xem toàn bộ thông tin chi tiết của một đơn hàng cụ thể bao gồm: thông tin người nhận hàng, địa chỉ giao hàng, phương thức thanh toán, trạng thái đơn hàng, danh sách từng sản phẩm đặt mua kèm đơn giá, số lượng, thành tiền và tổng tiền thanh toán để phục vụ xử lý và theo dõi đơn. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                                                     |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                                     |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công vào hệ thống với quyền quản trị viên.<br>- Đơn hàng cần xem đang tồn tại trong hệ thống.                                                                                                                                                                          |
| **Hậu điều kiện (Post-condition)** | - Toàn bộ thông tin chi tiết của đơn hàng được hiển thị đầy đủ, chính xác.<br>- Dữ liệu hệ thống không bị thay đổi.                                                                                                                                                                                      |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                              | Hệ thống                                                                                                                                                                                                         |
| ------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên nhấn nút "Xem chi tiết" tại một đơn hàng trên trang danh sách đơn hàng.                           |                                                                                                                                                                                                                  |
|                                                                                                                    | 2. Hệ thống truy vấn toàn bộ thông tin chi tiết của đơn hàng đã chọn (thông tin người nhận, trạng thái, danh sách sản phẩm và tổng tiền).                                                                        |
|                                                                                                                    | 3. Hệ thống hiển thị giao diện chi tiết đơn hàng bao gồm: Mã đơn hàng, Ngày đặt, Trạng thái, Thông tin người nhận (Họ tên, SĐT, Địa chỉ, Ghi chú), Phương thức thanh toán, Bảng danh sách món hàng và Tổng tiền. |
| 4. Quản trị viên xem các thông tin chi tiết của đơn hàng để thực hiện các thao tác quản lý và xử lý đơn tiếp theo. |                                                                                                                                                                                                                  |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 4.1.*

| Actor                                                               | Hệ thống                                                    |
| ------------------------------------------------------------------- | ----------------------------------------------------------- |
| 4.1. Quản trị viên nhấn nút "Quay lại" hoặc liên kết danh sách đơn. |                                                             |
|                                                                     | 4.2. Hệ thống chuyển về màn hình danh sách các đơn hàng.    |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 1.1.1, 2.1.1, 2.2.1.*

| Actor                                                                                   | Hệ thống                                                                                                                |
| --------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| 1.1.1. Phiên làm việc của Quản trị viên hết hạn hoặc tài khoản không có quyền quản trị. |                                                                                                                         |
|                                                                                         | 1.1.2. Hệ thống từ chối truy cập và yêu cầu người dùng đăng nhập lại tài khoản hợp lệ.                                  |
| 2.1.1. Đơn hàng cần xem không tồn tại hoặc đã bị xóa khỏi hệ thống.                     |                                                                                                                         |
|                                                                                         | 2.1.2. Hệ thống hiển thị thông báo lỗi: "Không tìm thấy thông tin đơn hàng yêu cầu!" và chuyển về danh sách đơn hàng.   |
| 2.2.1. Hệ thống gặp sự cố trong quá trình truy vấn chi tiết đơn hàng.                   |                                                                                                                         |
|                                                                                         | 2.2.2. Hệ thống hiển thị thông báo lỗi: "Không thể tải chi tiết đơn hàng lúc này. Vui lòng thử lại sau.".              |
