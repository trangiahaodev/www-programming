# Usecase: Xem, tìm kiếm và phân trang người dùng

| Thành phần | Nội dung |
| --- | --- |
| **Tên use case** | Xem, tìm kiếm và phân trang người dùng |
| **Mã use case** | uc007d-admin-view-users |
| **Mô tả sơ lược** | Admin tra cứu người dùng theo mã, họ tên hoặc email và xem từng trang kết quả. |
| **Actor chính** | Admin |
| **Actor phụ** | Không |
| **Tiền điều kiện (Pre-condition)** | Admin đã đăng nhập bằng tài khoản đang hoạt động và có quyền quản trị người dùng. |
| **Hậu điều kiện (Post-condition)** | Danh sách hiển thị đúng tiêu chí hợp lệ; không thay đổi thông tin người dùng và không hiển thị mật khẩu. |

### Luồng sự kiện chính (Main flow):

| Actor | Hệ thống |
| --- | --- |
| 1. Mở mục Người dùng trong khu vực quản trị. |  |
|  | 2. Kiểm tra quyền truy cập và hiển thị trang đầu gồm tối đa 10 người dùng, xếp mới nhất trước; mỗi dòng có mã, họ tên, email, điện thoại, vai trò và trạng thái hoạt động. |
| 3. Nhập từ khóa theo mã, tên hoặc email rồi chọn Tìm kiếm; có thể chọn số dòng mỗi trang hoặc chuyển trang. |  |
|  | 4. Kiểm tra từ khóa tối đa 100 ký tự, số dòng từ 1 đến 100 và trang không âm; tìm không phân biệt hoa thường, hiển thị danh sách phù hợp cùng điều khiển phân trang. |

### Luồng sự kiện thay thế (Alternate Flow):

| Actor | Hệ thống |
| --- | --- |
| 3.1. Để trống từ khóa và chọn Tìm kiếm. |  |
|  | 3.2. Hiển thị lại tất cả người dùng theo phân trang. Quay lại bước 3 nếu cần tra cứu tiếp. |
|  | 4.1. Không có người dùng khớp từ khóa. |
|  | 4.2. Hiển thị danh sách rỗng; Admin có thể đổi tiêu chí ở bước 3. |
|  | 4.3. Trang yêu cầu không còn dữ liệu, chẳng hạn sau khi xóa dòng cuối của trang. |
|  | 4.4. Hiển thị trang cuối còn dữ liệu, hoặc trang đầu rỗng nếu không còn kết quả. Kết thúc lần tra cứu. |

### Luồng sự kiện ngoại lệ (Exception Flow):

| Actor | Hệ thống |
| --- | --- |
|  | 2.1.1. Người truy cập chưa đăng nhập, tài khoản không còn hợp lệ hoặc không có quyền quản trị. |
|  | 2.1.2. Yêu cầu đăng nhập lại nếu chưa đăng nhập hoặc tài khoản hết hiệu lực; từ chối truy cập nếu đã đăng nhập nhưng không có quyền. Kết thúc use case. |
|  | 4.1.1. Từ khóa quá dài, số dòng ngoài 1–100 hoặc số trang âm. |
|  | 4.1.2. Thông báo bộ lọc không hợp lệ và hiển thị dữ liệu với tiêu chí mặc định: từ khóa trống, trang đầu, 10 dòng. Admin sửa tiêu chí ở bước 3. |
