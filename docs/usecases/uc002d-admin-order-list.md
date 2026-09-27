# Usecase: Quản trị viên xem danh sách đơn hàng

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                                         |
| ---------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên xem danh sách đơn hàng                                                                                                                                                                                                                                                                             |
| **Mã use case**                    | `uc002d-admin-order-list`                                                                                                                                                                                                                                                                                        |
| **Mô tả sơ lược**                  | Quản trị viên truy cập khu vực quản trị để xem danh sách toàn bộ các đơn hàng của khách hàng trong hệ thống dưới dạng bảng dữ liệu chi tiết (Mã đơn hàng, Tên khách hàng, Số điện thoại, Ngày đặt hàng, Số lượng sản phẩm, Tổng tiền, Trạng thái đơn hàng). Hỗ trợ tìm kiếm, lọc theo trạng thái và phân trang. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                                                            |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                                            |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công vào hệ thống với quyền quản trị viên.<br>- Quản trị viên đang ở khu vực quản trị hệ thống.                                                                                                                                                                              |
| **Hậu điều kiện (Post-condition)** | - Danh sách đơn hàng được hiển thị đầy đủ, chính xác theo điều kiện lọc/tìm kiếm và phân trang.<br>- Dữ liệu hệ thống không bị thay đổi.                                                                                                                                                                        |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                                                              | Hệ thống                                                                                                                                                                                          |
| -------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên chọn mục "Quản lý đơn hàng" trên menu quản trị.                                                                                   |                                                                                                                                                                                                   |
|                                                                                                                                                    | 2. Hệ thống truy vấn danh sách toàn bộ các đơn hàng trong hệ thống, sắp xếp theo thứ tự đặt hàng mới nhất.                                                                                        |
|                                                                                                                                                    | 3. Hệ thống hiển thị giao diện danh sách đơn hàng bao gồm: bảng dữ liệu (Mã đơn hàng, Khách hàng, SĐT, Ngày đặt, Số lượng món, Tổng tiền, Trạng thái), thanh tìm kiếm, bộ lọc trạng thái và phân trang. |
| 4. Quản trị viên nhập từ khóa tìm kiếm (mã đơn hàng, tên khách hàng, số điện thoại) hoặc chọn trạng thái đơn hàng cần lọc và nhấn nút "Tìm kiếm". |                                                                                                                                                                                                   |
|                                                                                                                                                    | 5. Hệ thống lọc và hiển thị danh sách các đơn hàng thỏa mãn điều kiện tìm kiếm/lọc.                                                                                                                |
| 6. Quản trị viên chọn chuyển sang trang khác trên thanh phân trang (nếu danh sách có nhiều trang).                                                 |                                                                                                                                                                                                   |
|                                                                                                                                                    | 7. Hệ thống tải dữ liệu đơn hàng của trang được chọn và cập nhật hiển thị bảng dữ liệu.                                                                                                           |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 4.1, 5.1.*

| Actor                                                         | Hệ thống                                                                                      |
| ------------------------------------------------------------- | --------------------------------------------------------------------------------------------- |
| 4.1. Quản trị viên nhấn nút xóa bộ lọc tìm kiếm hoặc làm mới. |                                                                                               |
|                                                               | 4.2. Hệ thống xóa trắng tiêu chí tìm kiếm và hiển thị lại toàn bộ đơn hàng ở trang đầu tiên.  |
| 5.1. Không có đơn hàng nào khớp với điều kiện tìm kiếm/lọc.   |                                                                                               |
|                                                               | 5.2. Hệ thống hiển thị thông báo "Không tìm thấy đơn hàng nào phù hợp" trên bảng dữ liệu.    |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 1.1.1, 2.1.1.*

| Actor                                                                                   | Hệ thống                                                                                                          |
| --------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------- |
| 1.1.1. Phiên làm việc của Quản trị viên hết hạn hoặc tài khoản không có quyền quản trị. |                                                                                                                   |
|                                                                                         | 1.1.2. Hệ thống từ chối truy cập và yêu cầu người dùng đăng nhập lại tài khoản hợp lệ.                            |
| 2.1.1. Hệ thống gặp sự cố trong quá trình truy vấn danh sách đơn hàng.                  |                                                                                                                   |
|                                                                                         | 2.1.2. Hệ thống hiển thị thông báo lỗi: "Không thể tải danh sách đơn hàng lúc này. Vui lòng thử lại sau.".        |
