# Usecase: Khách hàng xem trang chủ

| Thành phần                         | Nội dung                                                                                                                                                                                                                                           |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Khách hàng xem trang chủ                                                                                                                                                                                                                           |
| **Mã use case**                    | `uc004d-customer-view-home`                                                                                                                                                                                                                        |
| **Mô tả sơ lược**                  | Khách hàng truy cập trang chủ để khám phá các chiến dịch nổi bật, biểu ngữ (banner) khuyến mãi, danh sách sản phẩm nổi bật, các ưu đãi hấp dẫn, nhãn hàng đối tác và các bài viết làm đẹp mới nhất.                                              |
| **Actor chính**                    | Customer (Khách hàng / Khách vãng lai)                                                                                                                                                                                                             |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                              |
| **Tiền điều kiện (Pre-condition)** | Người dùng truy cập vào website của cửa hàng mỹ phẩm.                                                                                                                                                                                              |
| **Hậu điều kiện (Post-condition)** | - Toàn bộ giao diện trang chủ được hiển thị đầy đủ, trực quan với các sản phẩm nổi bật và banner quảng bá.<br>- Dữ liệu hệ thống không bị thay đổi.                                                                                               |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                            | Hệ thống                                                                                                                                                           |
| ------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1. Khách hàng truy cập vào trang chủ của website cửa hàng mỹ phẩm.                               |                                                                                                                                                                    |
|                                                                                                  | 2. Hệ thống truy vấn thông tin banner quảng cáo, danh sách sản phẩm nổi bật, sản phẩm ưu đãi và các tin tức mới nhất.                                              |
|                                                                                                  | 3. Hệ thống hiển thị giao diện trang chủ đầy đủ các khối nội dung: banner quảng bá, khối sản phẩm nổi bật, danh sách ưu đãi, thương hiệu đối tác và tin tức làm đẹp. |
| 4. Khách hàng lướt xem các phân khu nội dung, hình ảnh banner và danh sách các sản phẩm nổi bật. |                                                                                                                                                                    |
| 5. Khách hàng chọn xem chi tiết một sản phẩm nổi bật hiển thị trên trang chủ.                    |                                                                                                                                                                    |
|                                                                                                  | 6. Hệ thống chuyển sang màn hình hiển thị chi tiết của sản phẩm đã chọn.                                                                                           |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 4.1, 5.1.*

| Actor                                                                       | Hệ thống                                                                                   |
| --------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------ |
| 4.1. Khách hàng chọn xem một bài viết tin tức hoặc cẩm nang trên trang chủ. |                                                                                            |
|                                                                             | 4.2. Hệ thống chuyển sang màn hình xem nội dung chi tiết của bài viết được chọn.           |
| 5.1. Khách hàng làm mới trang chủ.                                          |                                                                                            |
|                                                                             | 5.2. Hệ thống cập nhật và hiển thị lại các sản phẩm nổi bật và ưu đãi mới nhất của cửa hàng. |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 2.1.1.*

| Actor                                                                                      | Hệ thống                                                                                                                                              |
| ------------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------- |
| 2.1.1. Hệ thống gặp sự cố trong quá trình tải dữ liệu sản phẩm nổi bật hoặc banner ưu đãi. |                                                                                                                                                       |
|                                                                                            | 2.1.2. Hệ thống hiển thị trang chủ với các nội dung cơ bản sẵn có và thông báo thân thiện: "Hệ thống đang cập nhật các ưu đãi nổi bật. Vui lòng quay lại sau.". |
