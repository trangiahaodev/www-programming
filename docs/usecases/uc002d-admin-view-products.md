# Usecase: Quản trị viên xem danh sách sản phẩm mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                            |
| ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên xem danh sách sản phẩm mỹ phẩm                                                                                                                                                                                                                                                        |
| **Mã use case**                    | `uc002d-admin-view-products`                                                                                                                                                                                                                                                                        |
| **Mô tả sơ lược**                  | Quản trị viên xem toàn bộ danh sách sản phẩm mỹ phẩm trong kho hàng hệ thống dưới dạng bảng dữ liệu chi tiết (Hình ảnh, Mã sản phẩm, Tên sản phẩm, Thương hiệu, Danh mục, Giá bán, Tồn kho, Trạng thái) để theo dõi và quản lý kho hàng. Hỗ trợ lọc theo danh mục, trạng thái và phân trang. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                                               |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                               |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công vào hệ thống với quyền quản trị viên.<br>- Quản trị viên đang ở khu vực quản trị hệ thống.                                                                                                                                                                  |
| **Hậu điều kiện (Post-condition)** | - Danh sách sản phẩm trong kho được hiển thị đầy đủ, chính xác theo điều kiện lọc và phân trang.<br>- Dữ liệu hệ thống không bị thay đổi.                                                                                                                                                         |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                            | Hệ thống                                                                                                                                                                                                 |
| ------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên chọn mục "Quản lý sản phẩm" trên menu quản trị.                                 |                                                                                                                                                                                                          |
|                                                                                                  | 2. Hệ thống truy vấn toàn bộ danh sách sản phẩm trong kho, sắp xếp theo thứ tự mới nhất.                                                                                                                |
|                                                                                                  | 3. Hệ thống hiển thị giao diện danh sách sản phẩm bao gồm: bảng dữ liệu (Hình ảnh, Mã sản phẩm, Tên, Thương hiệu, Danh mục, Giá bán, Tồn kho, Trạng thái), bộ lọc danh mục, nút thêm mới và phân trang. |
| 4. Quản trị viên chọn lọc theo danh mục hoặc trạng thái hoạt động để theo dõi sản phẩm.          |                                                                                                                                                                                                          |
|                                                                                                  | 5. Hệ thống lọc và hiển thị danh sách các sản phẩm thỏa mãn điều kiện lọc.                                                                                                                               |
| 6. Quản trị viên chọn chuyển sang trang khác trên thanh phân trang (nếu danh sách có nhiều trang). |                                                                                                                                                                                                          |
|                                                                                                  | 7. Hệ thống tải dữ liệu sản phẩm thuộc trang được chọn và cập nhật hiển thị bảng dữ liệu.                                                                                                                |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 4.1, 5.1.*

| Actor                                                          | Hệ thống                                                                                           |
| -------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- |
| 4.1. Quản trị viên nhấn nút xóa bộ lọc tìm kiếm hoặc làm mới. |                                                                                                    |
|                                                                | 4.2. Hệ thống xóa điều kiện lọc và hiển thị lại toàn bộ sản phẩm trong kho ở trang đầu tiên.       |
| 5.1. Không có sản phẩm nào khớp với điều kiện lọc.             |                                                                                                    |
|                                                                | 5.2. Hệ thống hiển thị thông báo "Không tìm thấy sản phẩm mỹ phẩm nào phù hợp" trên bảng dữ liệu. |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 1.1.1, 2.1.1.*

| Actor                                                                                   | Hệ thống                                                                                                                |
| --------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| 1.1.1. Phiên làm việc của Quản trị viên hết hạn hoặc tài khoản không có quyền quản trị. |                                                                                                                         |
|                                                                                         | 1.1.2. Hệ thống từ chối truy cập và yêu cầu người dùng đăng nhập lại tài khoản hợp lệ.                                  |
| 2.1.1. Hệ thống gặp sự cố trong quá trình truy vấn danh sách sản phẩm trong kho.        |                                                                                                                         |
|                                                                                         | 2.1.2. Hệ thống hiển thị thông báo lỗi: "Không thể tải danh sách sản phẩm mỹ phẩm lúc này. Vui lòng thử lại sau.".      |
