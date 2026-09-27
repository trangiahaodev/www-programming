# Usecase: Quản trị viên tìm kiếm sản phẩm mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                              |
| ---------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên tìm kiếm sản phẩm mỹ phẩm                                                                                                                                                                                                               |
| **Mã use case**                    | `uc002e-admin-search-products`                                                                                                                                                                                                                        |
| **Mô tả sơ lược**                  | Quản trị viên nhập từ khóa tìm kiếm vào thanh công cụ quản trị. Hệ thống xử lý tìm kiếm, lọc và hiển thị danh sách các sản phẩm mỹ phẩm khớp với tên sản phẩm hoặc mã sản phẩm hoặc thương hiệu để quản trị viên dễ dàng tra cứu và xử lý nghiệp vụ. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                 |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                 |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công vào hệ thống với quyền quản trị viên.<br>- Quản trị viên đang ở màn hình danh sách sản phẩm.                                                                                                                  |
| **Hậu điều kiện (Post-condition)** | - Bảng dữ liệu danh sách sản phẩm hiển thị các bản ghi khớp với từ khóa tìm kiếm.<br>- Dữ liệu hệ thống không bị thay đổi.                                                                                                                           |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                            | Hệ thống                                                                                                                                  |
| ------------------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên nhập từ khóa tìm kiếm vào ô tìm kiếm trên trang danh sách sản phẩm.             |                                                                                                                                           |
| 2. Quản trị viên nhấn nút "Tìm kiếm" (hoặc nhấn phím Enter).                                      |                                                                                                                                           |
|                                                                                                  | 3. Hệ thống lọc danh sách sản phẩm trong kho hàng có tên, mã sản phẩm hoặc thương hiệu khớp với từ khóa tìm kiếm.                         |
|                                                                                                  | 4. Hệ thống cập nhật bảng dữ liệu danh sách sản phẩm với các kết quả tìm thấy, hiển thị phân trang tương ứng và số lượng kết quả tìm được. |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 1.1, 4.1.*

| Actor                                                         | Hệ thống                                                                                                         |
| ------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| 1.1. Quản trị viên nhấn nút xóa từ khóa tìm kiếm hoặc làm mới. |                                                                                                                  |
|                                                               | 1.2. Hệ thống xóa ô tìm kiếm và hiển thị lại toàn bộ sản phẩm trong kho hàng ở trang đầu tiên.                   |
| 4.1. Không có sản phẩm nào khớp với từ khóa tìm kiếm.         |                                                                                                                  |
|                                                               | 4.2. Hệ thống hiển thị thông báo "Không tìm thấy sản phẩm mỹ phẩm nào phù hợp với từ khóa tìm kiếm" trên bảng dữ liệu. |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 1.1.1, 3.1.1.*

| Actor                                                                                   | Hệ thống                                                                                                             |
| --------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------- |
| 1.1.1. Phiên làm việc của Quản trị viên hết hạn hoặc tài khoản không có quyền quản trị. |                                                                                                                      |
|                                                                                         | 1.1.2. Hệ thống từ chối truy cập và yêu cầu người dùng đăng nhập lại tài khoản hợp lệ.                                |
| 3.1.1. Hệ thống gặp sự cố trong quá trình tìm kiếm dữ liệu.                             |                                                                                                                      |
|                                                                                         | 3.1.2. Hệ thống hiển thị thông báo lỗi: "Không thể thực hiện tìm kiếm sản phẩm lúc này. Vui lòng thử lại sau!".        |
