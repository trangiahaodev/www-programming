# Usecase: Quản trị viên xem danh sách danh mục mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                            |
| ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên xem danh sách danh mục mỹ phẩm                                                                                                                                                                                                        |
| **Mã use case**                    | `uc001d-admin-view-categories`                                                                                                                                                                                                                      |
| **Mô tả sơ lược**                  | Quản trị viên xem toàn bộ danh sách danh mục mỹ phẩm trong hệ thống dưới dạng bảng dữ liệu kèm các thông tin chi tiết (Mã danh mục, Tên danh mục, Mô tả, Số lượng sản phẩm liên kết, Trạng thái hoạt động). Hỗ trợ tìm kiếm danh mục và phân trang. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                               |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                               |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công vào hệ thống với quyền quản trị viên.<br>- Quản trị viên đang ở khu vực quản trị hệ thống.                                                                                                                  |
| **Hậu điều kiện (Post-condition)** | - Danh sách danh mục mỹ phẩm được hiển thị đầy đủ, chính xác theo điều kiện tìm kiếm và phân trang.<br>- Dữ liệu hệ thống không bị thay đổi.                                                                                                       |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                            | Hệ thống                                                                                                                                                                                          |
| ------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên chọn mục "Quản lý danh mục" trên menu quản trị.                                 |                                                                                                                                                                                                   |
|                                                                                                  | 2. Hệ thống truy vấn toàn bộ danh sách danh mục mỹ phẩm, sắp xếp theo thứ tự mới nhất.                                                                                                            |
|                                                                                                  | 3. Hệ thống hiển thị giao diện danh sách danh mục bao gồm: bảng dữ liệu (Mã danh mục, Tên danh mục, Mô tả, Số lượng sản phẩm, Trạng thái), ô tìm kiếm, nút thêm mới và thanh điều hướng phân trang. |
| 4. Quản trị viên nhập từ khóa tìm kiếm (theo mã hoặc tên danh mục) và nhấn nút "Tìm kiếm".       |                                                                                                                                                                                                   |
|                                                                                                  | 5. Hệ thống lọc danh sách theo từ khóa tìm kiếm và hiển thị lại bảng danh mục tương ứng.                                                                                                          |
| 6. Quản trị viên chọn chuyển sang trang khác trên thanh phân trang (nếu danh sách có nhiều trang). |                                                                                                                                                                                                   |
|                                                                                                  | 7. Hệ thống tải dữ liệu danh mục thuộc trang được chọn và cập nhật hiển thị.                                                                                                                      |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 4.1, 5.1.*

| Actor                                                          | Hệ thống                                                                                                                                    |
| -------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| 4.1. Quản trị viên nhấn nút xóa bộ lọc tìm kiếm hoặc làm mới. |                                                                                                                                             |
|                                                                | 4.2. Hệ thống xóa từ khóa tìm kiếm và hiển thị lại toàn bộ danh mục ở trang đầu tiên.                                                       |
| 5.1. Không có danh mục nào khớp với từ khóa tìm kiếm.          |                                                                                                                                             |
|                                                                | 5.2. Hệ thống hiển thị thông báo "Không tìm thấy danh mục mỹ phẩm nào phù hợp" trên bảng dữ liệu.                                           |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 1.1.1, 2.1.1.*

| Actor                                                                                   | Hệ thống                                                                                                                    |
| --------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------- |
| 1.1.1. Phiên làm việc của Quản trị viên hết hạn hoặc tài khoản không có quyền quản trị. |                                                                                                                             |
|                                                                                         | 1.1.2. Hệ thống từ chối truy cập và yêu cầu người dùng đăng nhập lại tài khoản hợp lệ.                                      |
| 2.1.1. Hệ thống gặp sự cố trong quá trình truy vấn danh sách danh mục.                  |                                                                                                                             |
|                                                                                         | 2.1.2. Hệ thống hiển thị thông báo lỗi: "Không thể tải danh sách danh mục mỹ phẩm lúc này. Vui lòng thử lại sau.".          |
