# Usecase: Cập nhật thông tin và trạng thái người dùng

| Thành phần | Nội dung |
| --- | --- |
| **Tên use case** | Cập nhật thông tin và trạng thái người dùng |
| **Mã use case** | uc007b-admin-update-user |
| **Mô tả sơ lược** | Admin sửa họ tên, email, điện thoại, địa chỉ và trạng thái hoạt động. Chức năng không cho đổi mã người dùng, mật khẩu hoặc vai trò. |
| **Actor chính** | Admin |
| **Actor phụ** | Không |
| **Tiền điều kiện (Pre-condition)** | Admin đã đăng nhập bằng tài khoản đang hoạt động và có quyền quản trị người dùng. |
| **Hậu điều kiện (Post-condition)** | Thành công: lưu các trường được phép sửa và hiển thị danh sách với thông báo cập nhật; khóa tài khoản có hiệu lực ở lần truy cập tiếp theo của người bị khóa.<br>Thất bại hoặc hủy: không lưu thay đổi. |

### Luồng sự kiện chính (Main flow):

| Actor | Hệ thống |
| --- | --- |
| 1. Chọn Sửa tại người dùng cần cập nhật trong danh sách. |  |
|  | 2. Kiểm tra quyền, tìm người dùng và hiển thị họ tên, email, điện thoại, địa chỉ, trạng thái hoạt động hiện tại trong biểu mẫu. |
| 3. Chỉnh họ tên, email, điện thoại, địa chỉ hoặc trạng thái hoạt động rồi chọn Lưu. |  |
|  | 4. Kiểm tra họ tên bắt buộc và tối đa 100 ký tự; email bắt buộc, đúng định dạng, tối đa 100 ký tự, không trùng tài khoản khác (không phân biệt hoa thường); điện thoại tùy chọn tối đa 20 ký tự, chỉ gồm chữ số, dấu cộng, khoảng trắng, ngoặc và gạch nối; địa chỉ tùy chọn tối đa 255 ký tự; trạng thái phải được xác định. Chặn tự khóa và khóa Admin hoạt động cuối cùng. Lưu thông tin, chuyển về danh sách và báo “Đã cập nhật người dùng.” |

### Luồng sự kiện thay thế (Alternate Flow):

| Actor | Hệ thống |
| --- | --- |
| 3.1. Chọn Hủy thay vì lưu. |  |
|  | 3.2. Trở về danh sách, không lưu thay đổi; kết thúc use case. |

### Luồng sự kiện ngoại lệ (Exception Flow):

| Actor | Hệ thống |
| --- | --- |
|  | 2.1.1. Người truy cập chưa đăng nhập, tài khoản không còn hợp lệ hoặc không có quyền quản trị. |
|  | 2.1.2. Yêu cầu đăng nhập lại nếu chưa đăng nhập hoặc tài khoản hết hiệu lực; từ chối truy cập nếu đã đăng nhập nhưng không có quyền. Kết thúc use case. |
|  | 2.2.1. Người dùng không tồn tại khi mở biểu mẫu. |
|  | 2.2.2. Trở về danh sách và báo không tìm thấy người dùng; kết thúc use case. |
|  | 4.1.1. Dữ liệu nhập thiếu hoặc không đạt các giới hạn tại bước 4. |
|  | 4.1.2. Giữ nội dung biểu mẫu và báo lỗi cạnh trường tương ứng. Quay lại bước 3. |
|  | 4.2.1. Email đã thuộc tài khoản khác hoặc phát sinh xung đột thông tin khi lưu. |
|  | 4.2.2. Không lưu; giữ biểu mẫu, hiển thị thông báo kiểm tra email. Quay lại bước 3. |
|  | 4.3.1. Admin chọn khóa chính tài khoản đang dùng hoặc khóa Admin hoạt động cuối cùng. |
|  | 4.3.2. Không lưu; giữ biểu mẫu và thông báo rõ quy tắc bị vi phạm. Quay lại bước 3. |
|  | 4.4.1. Người dùng đã bị xóa trước khi lưu thay đổi. |
|  | 4.4.2. Trở về danh sách, báo không tìm thấy người dùng; kết thúc use case. |
|  | 4.5.1. Yêu cầu gửi biểu mẫu không hợp lệ, hoặc quyền hay trạng thái tài khoản không còn hợp lệ khi gửi. |
|  | 4.5.2. Từ chối thao tác; yêu cầu đăng nhập lại nếu chưa đăng nhập hoặc tài khoản hết hiệu lực. Không lưu dữ liệu. Kết thúc use case; Admin cần mở lại chức năng bằng tài khoản hợp lệ. |
