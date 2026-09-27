# Usecase: Xóa người dùng

| Thành phần | Nội dung |
| --- | --- |
| **Tên use case** | Xóa người dùng |
| **Mã use case** | uc007c-admin-delete-user |
| **Mô tả sơ lược** | Admin xóa vĩnh viễn người dùng sau khi xác nhận trong hộp thoại ngay trên danh sách. Cấm xóa người dùng đã có bất kỳ đơn hàng nào, kể cả đơn đã hủy. |
| **Actor chính** | Admin |
| **Actor phụ** | Không |
| **Tiền điều kiện (Pre-condition)** | Admin đã đăng nhập bằng tài khoản đang hoạt động và có quyền quản trị người dùng. Admin đang xem danh sách người dùng. |
| **Hậu điều kiện (Post-condition)** | Thành công: người dùng bị xóa, danh sách giữ tiêu chí hợp lệ và thông báo kết quả một lần; nếu trang vừa rỗng thì hiển thị trang hợp lệ gần nhất.<br>Hủy hoặc bị từ chối: người dùng và đơn hàng giữ nguyên. Không chuyển sang ẩn tài khoản để vượt quy tắc xóa. |

### Luồng sự kiện chính (Main flow):

| Actor | Hệ thống |
| --- | --- |
| 1. Chọn Xóa tại người dùng muốn xóa trong danh sách. |  |
|  | 2. Hiển thị hộp thoại xác nhận ngay trên danh sách, nêu đúng họ tên và mã người dùng cùng hai nút Hủy và Xóa; chưa thực hiện xóa. |
| 3. Kiểm tra đúng người dùng và chọn Xóa trong hộp thoại để xác nhận. |  |
|  | 4. Kiểm tra quyền và xác nhận hợp lệ, người dùng còn tồn tại, không phải tài khoản đang dùng hoặc Admin hoạt động cuối cùng; kiểm tra người dùng chưa có đơn ở mọi trạng thái. Xóa người dùng, hiển thị lại danh sách giữ bộ lọc/số dòng và điều chỉnh trang nếu cần, báo “Đã xóa người dùng.” |

### Luồng sự kiện thay thế (Alternate Flow):

| Actor | Hệ thống |
| --- | --- |
| 3.1. Chọn Hủy hoặc nhấn Escape khi hộp thoại đang mở. |  |
|  | 3.2. Đóng hộp thoại, trả vị trí thao tác về nút đã mở; không gửi yêu cầu xóa. Kết thúc use case. |
| 3.3. Bấm Xóa lặp lại khi yêu cầu xác nhận đang được xử lý. |  |
|  | 3.4. Không gửi thêm yêu cầu từ cùng biểu mẫu; tiếp tục chờ kết quả của bước 4. |

### Luồng sự kiện ngoại lệ (Exception Flow):

| Actor | Hệ thống |
| --- | --- |
|  | 4.1.1. Yêu cầu thiếu xác nhận, bộ lọc gửi kèm vượt giới hạn hoặc không còn được phép thực hiện thao tác. |
|  | 4.1.2. Không xóa. Nếu xác nhận/bộ lọc sai, trở về danh sách mặc định và báo “Yêu cầu xóa không hợp lệ. Vui lòng mở lại hộp xác nhận.” Nếu chưa đăng nhập hoặc tài khoản hết hiệu lực, yêu cầu đăng nhập lại; nếu thiếu quyền hoặc biểu mẫu không hợp lệ về nguồn gửi thì từ chối truy cập. Kết thúc use case. |
|  | 4.2.1. Người dùng không còn tồn tại, ví dụ đã bị xóa bởi yêu cầu trước. |
|  | 4.2.2. Trở về danh sách giữ bộ lọc hợp lệ và báo không tìm thấy người dùng; kết thúc use case. |
|  | 4.3.1. Đích xóa là chính tài khoản Admin đang dùng hoặc Admin hoạt động cuối cùng. |
|  | 4.3.2. Không xóa; trở về danh sách giữ bộ lọc và thông báo rõ quy tắc bị vi phạm; kết thúc use case. |
|  | 4.4.1. Người dùng có ít nhất một đơn hàng, bao gồm đơn đã hủy. |
|  | 4.4.2. Không xóa; trở về danh sách giữ bộ lọc và báo “Không thể xóa người dùng đã có đơn hàng, kể cả đơn đã hủy.” Kết thúc use case. |
|  | 4.5.1. Phát sinh dữ liệu liên quan hoặc thao tác cập nhật đồng thời khiến không thể hoàn tất xóa. |
|  | 4.5.2. Không xóa; trở về danh sách giữ bộ lọc, báo người dùng đang có dữ liệu liên quan hoặc đang được cập nhật và đề nghị tải lại/thử lại. Kết thúc use case. |
