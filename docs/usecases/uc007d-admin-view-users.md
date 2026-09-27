# Usecase: Quản trị viên xem danh sách người dùng

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                 |
| ---------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Tên use case**                   | Quản trị viên xem danh sách người dùng                                                                                                                                                                                                                                   |
| **Mã use case**                    | `uc007d-admin-view-users`                                                                                                                                                                                                                                                |
| **Mô tả sơ lược**                  | Quản trị viên truy cập khu vực quản trị để xem toàn bộ danh sách tài khoản người dùng trong hệ thống dưới dạng bảng dữ liệu chi tiết (Mã người dùng, Họ tên, Email, Số điện thoại, Địa chỉ, Vai trò, Trạng thái hoạt động, Ngày tạo). Hỗ trợ tìm kiếm và phân trang. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                    |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                    |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công vào hệ thống với quyền quản trị viên.<br>- Quản trị viên đang ở khu vực quản trị hệ thống.                                                                                                                                      |
| **Hậu điều kiện (Post-condition)** | - Toàn bộ danh sách tài khoản người dùng hiển thị đầy đủ, chính xác theo điều kiện tìm kiếm và phân trang.<br>- Dữ liệu hệ thống không bị thay đổi.                                                                                                                      |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                            | Hệ thống                                                                                                                                                                                          |
| ------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên chọn mục "Quản lý người dùng" trên menu quản trị.                               |                                                                                                                                                                                                   |
|                                                                                                  | 2. Hệ thống truy vấn toàn bộ danh sách tài khoản người dùng trong hệ thống, sắp xếp theo thứ tự mới nhất.                                                                                        |
|                                                                                                  | 3. Hệ thống hiển thị giao diện danh sách người dùng bao gồm: bảng dữ liệu (Mã người dùng, Họ tên, Email, SĐT, Địa chỉ, Vai trò, Trạng thái), thanh tìm kiếm, bộ lọc vai trò và thanh phân trang. |
| 4. Quản trị viên nhập từ khóa tìm kiếm (theo họ tên, email hoặc số điện thoại) và nhấn nút "Tìm kiếm". |                                                                                                                                                                                                   |
|                                                                                                  | 5. Hệ thống lọc và hiển thị danh sách các tài khoản người dùng thỏa mãn điều kiện tìm kiếm.                                                                                                       |
| 6. Quản trị viên chọn chuyển sang trang khác trên thanh phân trang (nếu danh sách có nhiều trang). |                                                                                                                                                                                                   |
|                                                                                                  | 7. Hệ thống tải dữ liệu người dùng của trang được chọn và cập nhật hiển thị bảng dữ liệu.                                                                                                         |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 4.1, 5.1.*

| Actor                                                         | Hệ thống                                                                                          |
| ------------------------------------------------------------- | ------------------------------------------------------------------------------------------------- |
| 4.1. Quản trị viên nhấn nút xóa bộ lọc tìm kiếm hoặc làm mới. |                                                                                                   |
|                                                               | 4.2. Hệ thống xóa điều kiện tìm kiếm và hiển thị lại toàn bộ tài khoản người dùng ở trang đầu tiên. |
| 5.1. Không có tài khoản nào khớp với từ khóa tìm kiếm.        |                                                                                                   |
|                                                               | 5.2. Hệ thống hiển thị thông báo "Không tìm thấy người dùng nào phù hợp" trên bảng dữ liệu.      |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 1.1.1, 2.1.1.*

| Actor                                                                                   | Hệ thống                                                                                                              |
| --------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| 1.1.1. Phiên làm việc của Quản trị viên hết hạn hoặc tài khoản không có quyền quản trị. |                                                                                                                       |
|                                                                                         | 1.1.2. Hệ thống từ chối truy cập và yêu cầu người dùng đăng nhập lại tài khoản hợp lệ.                                |
| 2.1.1. Hệ thống gặp sự cố trong quá trình truy vấn danh sách người dùng.                |                                                                                                                       |
|                                                                                         | 2.1.2. Hệ thống hiển thị thông báo lỗi: "Không thể tải danh sách người dùng lúc này. Vui lòng thử lại sau.".          |
