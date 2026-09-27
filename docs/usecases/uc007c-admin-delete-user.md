# Usecase: Quản trị viên xóa người dùng

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                   |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên xóa người dùng                                                                                                                                                                                                                                               |
| **Mã use case**                    | `uc007c-admin-delete-user`                                                                                                                                                                                                                                                 |
| **Mô tả sơ lược**                  | Quản trị viên thực hiện xóa một tài khoản người dùng khỏi hệ thống. Hệ thống yêu cầu xác nhận thao tác, kiểm tra lịch sử mua hàng của người dùng trước khi tiến hành xóa (nếu đã có đơn hàng thì từ chối xóa để bảo toàn dữ liệu giao dịch; nếu chưa có thì xóa tài khoản). |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                      |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                      |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công vào hệ thống với quyền quản trị viên.<br>- Tài khoản người dùng cần xóa đang tồn tại và hiển thị trên giao diện quản trị.                                                                                                         |
| **Hậu điều kiện (Post-condition)** | - Tài khoản người dùng được xóa hoàn toàn khỏi hệ thống nếu không có lịch sử mua hàng.<br>- Hệ thống hiển thị lại danh sách người dùng sau khi xóa kèm thông báo kết quả.                                                                                                  |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                            | Hệ thống                                                                                                                                      |
| ------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên nhấn nút "Xóa" tại một tài khoản trên trang danh sách người dùng.               |                                                                                                                                               |
|                                                                                                  | 2. Hệ thống hiển thị hộp thoại cảnh báo và yêu cầu xác nhận thao tác xóa tài khoản người dùng.                                                |
| 3. Quản trị viên nhấn nút "Xác nhận xóa" trên hộp thoại xác nhận.                                 |                                                                                                                                               |
|                                                                                                  | 4. Hệ thống kiểm tra các quy tắc an toàn và kiểm tra lịch sử mua hàng của tài khoản người dùng.                                               |
|                                                                                                  | 5. Hệ thống xác nhận người dùng chưa từng phát sinh đơn hàng nào, tiến hành xóa tài khoản người dùng khỏi hệ thống.                           |
|                                                                                                  | 6. Hệ thống cập nhật lại danh sách người dùng và hiển thị thông báo thành công: "Xóa tài khoản người dùng thành công!".                       |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 3.1.*

| Actor                                                                               | Hệ thống                                                                                           |
| ----------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- |
| 3.1. Quản trị viên nhấn nút "Hủy bỏ" hoặc nhấp chuột ra ngoài hộp thoại xác nhận.   |                                                                                                    |
|                                                                                     | 3.2. Hệ thống đóng hộp thoại xác nhận, không thực hiện thao tác xóa và giữ nguyên trang danh sách. |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 1.1.1, 4.1.1, 4.2.1, 4.3.1, 4.4.1, 5.1.1.*

| Actor                                                                                   | Hệ thống                                                                                                                                                                                                |
| --------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1.1.1. Phiên làm việc của Quản trị viên hết hạn hoặc tài khoản không có quyền quản trị. |                                                                                                                                                                                                         |
|                                                                                         | 1.1.2. Hệ thống từ chối truy cập và yêu cầu người dùng đăng nhập lại tài khoản hợp lệ.                                                                                                                  |
| 4.1.1. Tài khoản người dùng cần xóa không tồn tại hoặc đã bị xóa trước đó.              |                                                                                                                                                                                                         |
|                                                                                         | 4.1.2. Hệ thống hiển thị thông báo lỗi: "Không tìm thấy người dùng cần xóa!" trên trang danh sách.                                                                                                      |
| 4.2.1. Quản trị viên thao tác xóa tài khoản của chính mình.                             |                                                                                                                                                                                                         |
|                                                                                         | 4.2.2. Hệ thống từ chối xóa và hiển thị cảnh báo: "Bạn không thể tự xóa tài khoản của chính mình!".                                                                                                     |
| 4.3.1. Tài khoản cần xóa là Quản trị viên duy nhất còn lại trong hệ thống.              |                                                                                                                                                                                                         |
|                                                                                         | 4.3.2. Hệ thống từ chối xóa và hiển thị cảnh báo: "Không thể xóa vì hệ thống phải có ít nhất một Quản trị viên đang hoạt động!".                                                                       |
| 4.4.1. Tài khoản người dùng đã có lịch sử đơn hàng trong hệ thống.                      |                                                                                                                                                                                                         |
|                                                                                         | 4.4.2. Hệ thống từ chối xóa tài khoản, giữ nguyên dữ liệu và hiển thị thông báo lỗi: "Không thể xóa tài khoản này vì đã có lịch sử đơn hàng! Bạn có thể chuyển sang trạng thái Khóa tài khoản.". |
| 5.1.1. Hệ thống gặp sự cố trong quá trình xóa dữ liệu.                                  |                                                                                                                                                                                                         |
|                                                                                         | 5.1.2. Hệ thống hủy bỏ thao tác xóa và hiển thị thông báo lỗi: "Không thể xóa tài khoản lúc này do lỗi hệ thống. Vui lòng thử lại sau!".                                                              |
