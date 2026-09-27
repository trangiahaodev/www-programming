# Usecase: Khách hàng xem chi tiết sản phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                           |
| ---------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Khách hàng xem chi tiết sản phẩm                                                                                                                                                                                                                                                   |
| **Mã use case**                    | `uc003d-customer-view-product-detail`                                                                                                                                                                                                                                              |
| **Mô tả sơ lược**                  | Khách hàng xem toàn bộ thông tin chi tiết của một sản phẩm mỹ phẩm bao gồm: hình ảnh sắc nét, tên sản phẩm, thương hiệu, giá bán, mức giảm giá, tình trạng tồn kho, xuất xứ, mô tả chi tiết, thành phần dưỡng chất, hướng dẫn sử dụng và đánh giá thực tế từ khách hàng trước. |
| **Actor chính**                    | Customer (Khách hàng / Khách vãng lai)                                                                                                                                                                                                             |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                              |
| **Tiền điều kiện (Pre-condition)** | Khách hàng nhấn chọn một sản phẩm từ danh sách sản phẩm, trang chủ hoặc tìm kiếm.                                                                                                                                                                                                  |
| **Hậu điều kiện (Post-condition)** | - Thông tin chi tiết của sản phẩm được hiển thị đầy đủ, chính xác và trực quan.<br>- Dữ liệu hệ thống không bị thay đổi.                                                                                                                                                           |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                             | Hệ thống                                                                                                                                                                                                                  |
| ----------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Khách hàng chọn xem một sản phẩm mỹ phẩm trên màn hình danh sách sản phẩm hoặc trang chủ.                      |                                                                                                                                                                                                                           |
|                                                                                                                   | 2. Hệ thống truy vấn toàn bộ thông tin chi tiết của sản phẩm đã chọn (hình ảnh, tên, giá, thương hiệu, tồn kho, xuất xứ, mô tả, thành phần, hướng dẫn sử dụng) và danh sách các sản phẩm liên quan cùng danh mục.       |
|                                                                                                                   | 3. Hệ thống hiển thị màn hình chi tiết sản phẩm bao gồm: hình ảnh lớn, thương hiệu, tên sản phẩm, giá bán, tình trạng còn hàng, nguồn gốc xuất xứ, các tab thông tin mô tả chi tiết, hướng dẫn sử dụng và đánh giá. |
| 4. Khách hàng xem xét các thông tin chi tiết, thành phần và hướng dẫn sử dụng của sản phẩm.                       |                                                                                                                                                                                                                           |
| 5. Khách hàng lựa chọn số lượng cần mua và nhấn nút "Thêm vào giỏ hàng".                                          |                                                                                                                                                                                                                           |
|                                                                                                                   | 6. Hệ thống ghi nhận sản phẩm vào giỏ hàng và hiển thị thông báo thành công: "Thêm sản phẩm vào giỏ hàng thành công!".                                                                                                   |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 3.1, 4.1.*

| Actor                                                                     | Hệ thống                                                                                      |
| ------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------- |
| 3.1. Sản phẩm đã hết hàng trong kho.                                      |                                                                                               |
|                                                                           | 3.2. Hệ thống hiển thị nhãn thông báo "Tạm thời hết hàng" và khóa thao tác thêm vào giỏ hàng. |
| 4.1. Khách hàng nhấn chọn xem một sản phẩm gợi ý liên quan ở bên dưới.    |                                                                                               |
|                                                                           | 4.2. Hệ thống chuyển sang màn hình hiển thị chi tiết của sản phẩm liên quan vừa được chọn.    |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 2.1.1, 2.2.1.*

| Actor                                                                                   | Hệ thống                                                                                                                |
| --------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| 2.1.1. Sản phẩm yêu cầu xem không tồn tại hoặc đã bị gỡ khỏi hệ thống.                 |                                                                                                                         |
|                                                                                         | 2.1.2. Hệ thống hiển thị thông báo lỗi: "Không tìm thấy thông tin sản phẩm yêu cầu!" và chuyển về danh sách sản phẩm.   |
| 2.2.1. Hệ thống gặp sự cố trong quá trình truy vấn thông tin chi tiết sản phẩm.         |                                                                                                                         |
|                                                                                         | 2.2.2. Hệ thống hiển thị thông báo lỗi: "Không thể tải thông tin sản phẩm lúc này. Vui lòng thử lại sau.".              |
