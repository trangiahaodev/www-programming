# Usecase: Khách hàng xem danh sách sản phẩm mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                      |
| ---------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Khách hàng xem danh sách sản phẩm mỹ phẩm                                                                                                                                                                                                                                     |
| **Mã use case**                    | `uc002d-customer-view-products`                                                                                                                                                                                                                                               |
| **Mô tả sơ lược**                  | Khách hàng truy cập website để xem danh sách các dòng mỹ phẩm đang mở bán dưới dạng lưới sản phẩm (hình ảnh, tên, thương hiệu, giá bán, nhãn giảm giá/mới), hỗ trợ lọc theo danh mục, sắp xếp theo tiêu chí và tìm kiếm nhanh để lướt xem và lựa chọn sản phẩm phù hợp. |
| **Actor chính**                    | Customer (Khách hàng / Khách vãng lai)                                                                                                                                                                                                                                        |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                         |
| **Tiền điều kiện (Pre-condition)** | Người dùng truy cập vào website cửa hàng mỹ phẩm.                                                                                                                                                                                                                             |
| **Hậu điều kiện (Post-condition)** | - Danh sách sản phẩm mỹ phẩm được hiển thị trực quan theo tiêu chí khách hàng lựa chọn.<br>- Dữ liệu hệ thống không bị thay đổi.                                                                                                                                             |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                                            | Hệ thống                                                                                                                                                                                                |
| -------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Khách hàng chọn xem trang "Sản phẩm" trên thanh điều hướng của website.                                                       |                                                                                                                                                                                                         |
|                                                                                                                                  | 2. Hệ thống truy vấn danh sách danh mục hoạt động và danh sách các sản phẩm mỹ phẩm đang mở bán ở trang đầu tiên.                                                                                      |
|                                                                                                                                  | 3. Hệ thống hiển thị giao diện danh sách sản phẩm bao gồm: thanh chọn danh mục, thanh sắp xếp tiêu chí và lưới thẻ sản phẩm (hình ảnh, thương hiệu, tên sản phẩm, giá bán, mức giảm giá, đánh giá). |
| 4. Khách hàng lướt xem các thẻ sản phẩm hiển thị trên trang.                                                                     |                                                                                                                                                                                                         |
| 5. Khách hàng chọn một danh mục trên thanh danh mục để xem sản phẩm theo nhóm.                                                    |                                                                                                                                                                                                         |
|                                                                                                                                  | 6. Hệ thống lọc các sản phẩm thuộc danh mục được chọn và hiển thị lại lưới sản phẩm tương ứng.                                                                                                         |
| 7. Khách hàng chọn chuyển trang trên thanh điều hướng phân trang (nếu danh sách có nhiều trang).                                 |                                                                                                                                                                                                         |
|                                                                                                                                  | 8. Hệ thống tải dữ liệu các sản phẩm ở trang được chọn và cập nhật hiển thị.                                                                                                                           |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 4.1, 5.1, 6.1.*

| Actor                                                                                       | Hệ thống                                                                                                                              |
| ------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| 4.1. Khách hàng nhập từ khóa vào ô tìm kiếm sản phẩm và nhấn nút tìm kiếm.                  |                                                                                                                                       |
|                                                                                             | 4.2. Hệ thống lọc và hiển thị các sản phẩm có tên hoặc thương hiệu khớp với từ khóa tìm kiếm.                                         |
| 5.1. Khách hàng thay đổi tiêu chí sắp xếp sản phẩm (Giá tăng dần, Giá giảm dần, Mới nhất).  |                                                                                                                                       |
|                                                                                             | 5.2. Hệ thống sắp xếp lại danh sách sản phẩm theo tiêu chí được chọn và cập nhật hiển thị.                                            |
| 6.1. Không có sản phẩm nào thuộc danh mục đã chọn hoặc khớp với từ khóa tìm kiếm.           |                                                                                                                                       |
|                                                                                             | 6.2. Hệ thống hiển thị thông báo "Không tìm thấy sản phẩm nào phù hợp" kèm gợi ý cho phép khách hàng nhấn xem lại tất cả sản phẩm.   |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 2.1.1, 8.1.1.*

| Actor                                                               | Hệ thống                                                                                                              |
| ------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| 2.1.1. Hệ thống gặp sự cố trong quá trình tải danh sách sản phẩm.   |                                                                                                                       |
|                                                                     | 2.1.2. Hệ thống hiển thị thông báo lỗi: "Không thể tải danh sách sản phẩm lúc này. Vui lòng thử lại sau.".            |
| 8.1.1. Hệ thống gặp sự cố khi chuyển trang dữ liệu.                 |                                                                                                                       |
|                                                                     | 8.1.2. Hệ thống hiển thị thông báo lỗi và giữ nguyên trạng thái trang hiện tại để khách hàng không bị gián đoạn xem. |
