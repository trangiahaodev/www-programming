# Usecase: Khách hàng trải nghiệm trang chủ PinkyCloud

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                                        |
| ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Khách hàng trải nghiệm trang chủ PinkyCloud                                                                                                                                                                                                                                                                     |
| **Mã use case**                    | `uc004d-customer-view-home`                                                                                                                                                                                                                                                                                     |
| **Mô tả sơ lược**                  | Khách hàng truy cập trang chủ của website PinkyCloud để khám phá các chiến dịch nổi bật: Banner hero tràn viền toàn trang, Khối 4 sản phẩm nổi bật Bento Grid phá cách (tự động random ngẫu nhiên mỗi lần tải lại trang), Khối Flash Deals giờ vàng (6 sản phẩm cùng hàng), Băng chuyền nhãn hàng đối tác, Hệ thống chi nhánh showroom và Các bài viết cẩm nang làm đẹp. |
| **Actor chính**                    | Khách hàng (Customer / Khách vãng lai)                                                                                                                                                                                                                                                                          |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                                           |
| **Tiền điều kiện (Pre-condition)** | Người dùng mở trình duyệt và truy cập vào địa chỉ gốc `/` hoặc `/trang-chu` của website PinkyCloud.                                                                                                                                                                                                           |
| **Hậu điều kiện (Post-condition)** | Toàn bộ giao diện trang chủ được hiển thị đầy đủ, mượt mà và trực quan; dữ liệu Bento Grid được xáo trộn ngẫu nhiên sinh động.                                                                                                                                                                                 |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                 | Hệ thống                                                                                                                                                                                                                                                                      |
| ----------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Khách hàng truy cập trang chủ qua đường dẫn `/` hoặc click logo PinkyCloud trên thanh điều hướng.   |                                                                                                                                                                                                                                                                               |
|                                                                                                       | 2. Hệ thống tiếp nhận yêu cầu (`GET /`), đồng thời gọi các dịch vụ backend:<br>- `getRandomFeaturedProducts(4)`: truy vấn toàn bộ sản phẩm hoạt động, xáo trộn ngẫu nhiên (`Collections.shuffle`) và chọn 4 sản phẩm cho Bento Grid.<br>- `getHotProducts(6)`: lấy 6 sản phẩm giảm giá cao nhất cho Flash Deals.<br>- `getOffices()`: lấy danh sách các chi nhánh showroom.<br>- `getNews()`: lấy các bài viết cẩm nang làm đẹp mới nhất. |
|                                                                                                       | 3. Hệ thống render giao diện trang chủ (`customer/home`) đầy đủ các phân khu chức năng: Hero Banner chuyển động, Bento Grid 4 sản phẩm, Flash Deals 6 sản phẩm/hàng, Băng chuyền thương hiệu có nút cuộn & modal tra cứu, Hệ thống Showroom và Tin tức bài viết nổi bật.   |
| 4. Khách hàng lướt xem các phân khu, có thể bấm vào banner, xem chi tiết sản phẩm, lưu mã voucher hoặc tra cứu địa chỉ chi nhánh gần nhất. |                                                                                                                                                                                                                                               |
| 5. Khi khách hàng tải lại trang (F5 / Reload).                                                         |                                                                                                                                                                                                                                                                               |
|                                                                                                       | 6. Hệ thống thực hiện lại quy trình xáo trộn ngẫu nhiên và hiển thị 4 sản phẩm mới trên khối Bento Grid, giúp trải nghiệm duyệt web luôn mới mẻ.                                                                                                                              |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 4.1, 4.2.*

| Actor                                                                                          | Hệ thống                                                                                                                                                              |
| ---------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 4.1. Khách hàng click vào một sản phẩm trong Bento Grid hoặc Flash Deals.                      |                                                                                                                                                                       |
|                                                                                                | 4.2. Hệ thống chuyển hướng khách hàng tới trang chi tiết sản phẩm (`/san-pham/{id}`) tương ứng.                                                                      |
| 4.3. Khách hàng bấm nút cuộn `<` hoặc `>` trên thanh nhãn hàng hoặc bấm nút "Xem tất cả thương hiệu". |                                                                                                                                                                       |
|                                                                                                | 4.4. Trình duyệt cuộn mượt thanh nhãn hàng hoặc hiển thị Modal danh bạ tất cả thương hiệu A-Z kèm ô tìm kiếm lọc nhanh theo thời gian thực.                             |
| 4.5. Khách hàng bấm nút "Sao chép mã" trên các thẻ Voucher khuyến mãi.                         |                                                                                                                                                                       |
|                                                                                                | 4.6. JavaScript sao chép mã ưu đãi vào clipboard của khách hàng và hiển thị thông báo Toast "Đã sao chép mã voucher thành công".                                      |
| 4.7. Khách hàng bấm xem bản đồ hoặc gọi hotline của một chi nhánh showroom.                  |                                                                                                                                                                       |
|                                                                                                | 4.8. Trình duyệt mở liên kết gọi điện `tel:...` hoặc chuyển tiếp tới vị trí bản đồ showroom tương ứng.                                                                |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 2.1.1.*

| Actor                                                                            | Hệ thống                                                                                                                                       |
| -------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| 2.1.1. Lỗi kết nối cơ sở dữ liệu khi tải sản phẩm nổi bật hoặc flash deals.      |                                                                                                                                                |
|                                                                                  | 2.1.2. Hệ thống bắt lỗi (fallback), hiển thị trang chủ với các phân khu nội dung tĩnh và thông báo nhẹ "Đang cập nhật danh mục ưu đãi hôm nay."|
|                                                                                  | 2.1.3. Kết thúc use case.                                                                                                                      |

---

## 🛠 Yêu cầu cập nhật Database / Entity / DTO

### 1. DTOs Phục Vụ Trang Chủ
- **`ProductCardDTO`**: Dữ liệu hiển thị thẻ sản phẩm cho Bento Grid và Flash Deals.
- **`OfficeDTO`**:
  - `id`: Mã chi nhánh
  - `name`: Tên showroom (Showroom Quận 1 - Flagship, Showroom Hoàn Kiếm, Showroom Hải Châu...)
  - `address`: Địa chỉ chi tiết
  - `phone`: Hotline hỗ trợ
  - `openingHours`: Giờ mở cửa (08:30 - 22:00)
  - `image`: Hình ảnh không gian showroom
- **`NewsDTO`**:
  - `id`, `title`, `excerpt`, `author`, `publishedDate`, `image`, `tag`
- **`VoucherDTO`**:
  - `code`, `title`, `discountText`, `minOrderText`, `expiryDate`

### 2. Controller & Service Requirements
- **Endpoint**: `GET /` hoặc `GET /trang-chu`
- **Service Methods**:
  - `ProductService.getRandomFeaturedProducts(int limit)`: Random ngẫu nhiên sản phẩm mỗi lần request.
  - `ProductService.getHotProducts(int limit)`: Lấy sản phẩm Flash Deals theo phần trăm giảm giá và số lượng bán.
  - `HomeContentService.getOffices()`: Danh sách chi nhánh.
  - `HomeContentService.getNews()`: Tin tức làm đẹp.
  - `HomeContentService.getVouchers()`: Voucher độc quyền.
- **View trả về**: `customer/home`
