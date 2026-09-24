# 🎨 UX/UI Blueprint: admin-order-detail-d (Xem chi tiết đơn hàng)

Tài liệu thiết kế trải nghiệm người dùng (UX Blueprint) đạt chuẩn Senior UX/UI (Google UX Design Certified) cho giao diện Xem chi tiết đơn hàng dành cho Quản trị viên trong hệ thống bán mỹ phẩm trực tuyến.

---

### 1. 🎯 User Goal & Context
- **Đối tượng người dùng:** Quản trị viên bán hàng, Trưởng kho vận hành và Chuyên viên Chăm sóc khách hàng (Admin / Warehouse & CS Specialist).
- **Trạng thái tâm lý:**
  - Cần tra cứu nhanh và chính xác: Đang xử lý cuộc gọi hotline khiếu nại của khách, kiểm tra hàng xuất kho hoặc đối soát tiền thanh toán.
  - Yêu cầu giảm thiểu sai sót: Cần nhìn thấy ngay thông tin người nhận, địa chỉ giao hàng, danh sách sản phẩm snapshot và tổng số tiền phải thu.
  - Không muốn bị gián đoạn luồng làm việc: Cần lối thoát nhanh ("Quay lại danh sách") để xử lý tiếp các đơn hàng khác.
- **Mục tiêu cốt lõi trong 3 giây đầu tiên:**
  - **Mã đơn & Trạng thái:** Nhận biết ngay mã đơn hàng `#ORD...` và Badge trạng thái hiện tại (màu sắc trực quan theo chuẩn tâm lý màu học).
  - **Khách hàng & Liên hệ:** Tên khách hàng in đậm, số điện thoại rõ ràng có thể bấm gọi ngay hoặc sao chép.
  - **Tình trạng thanh toán:** Đã thanh toán hay chưa, hình thức thanh toán là gì (COD, VNPay, Chuyển khoản ngân hàng).
  - **Tổng giá trị đơn:** Số tiền thanh toán và tổng số lượng sản phẩm nổi bật, rõ ràng.

---

### 2. 🧠 "Don't Make Me Think" (Giảm tải nhận thức & Tối ưu thao tác)
- **Predictive Actions (Dự đoán hành động):**
  - **Click-to-Call & Quick Contact:** Số điện thoại khách hàng được gắn link `tel:` giúp nhân viên CSKH/Admin nhấp gọi nhanh trên thiết bị hỗ trợ hoặc sao chép nhanh với 1 thao tác.
  - **Nhận diện màu sắc tức thì:** Sử dụng cùng bộ quy tắc Color Psychology như màn hình danh sách đơn hàng giúp người dùng không cần phải đọc chữ vẫn biết trạng thái đơn.
  - **Lối thoát điều hướng tự nhiên:** Nút "Quay lại danh sách" được đặt nổi bật ở góc trên bên phải trang, đồng thời thanh breadcrumb giúp di chuyển linh hoạt về bất kỳ cấp quản lý nào.
- **Error Prevention & Snapshot Transparency (Minh bạch dữ liệu & Phòng ngừa lỗi):**
  - **Dữ liệu Snapshot rõ ràng:** Tên sản phẩm, mã sản phẩm và đơn giá hiển thị là dữ liệu lưu trữ tại thời điểm đặt hàng, đảm bảo tính pháp lý và không bị ảnh hưởng nếu sản phẩm trong kho thay đổi giá sau này.
  - **Empty State tinh tế:** Nếu đơn hàng không có mặt hàng nào (Ngoại lệ 2.5), hiển thị bảng kèm Empty State thông báo lịch sự: *"Đơn hàng hiện chưa có sản phẩm nào được ghi nhận."* thay vì để bảng trống trơn.
  - **Giao diện chỉ đọc (Read-only Guard):** Toàn bộ giao diện được thiết kế dạng thẻ thông tin và bảng chi tiết chỉ đọc, không có các nút thao tác xóa/sửa nguy hiểm gây nhầm lẫn khi chỉ cần tra cứu thông tin.

---

### 3. 📐 Visual & Layout Strategy

#### A. Bố cục tổng thể (Visual Flow & Information Architecture):
1. **Breadcrumb & Header Area:**
   - Đường dẫn điều hướng: `Trang chủ / Quản lý bán hàng / Đơn hàng / #ORD26000001`.
   - Tiêu đề trang: **Đơn hàng #ORD26000001** (Font lớn, in đậm) đặt cạnh **Status Badge**.
   - Dòng phụ: Thời điểm đặt hàng (`Ngày tạo đơn: 24/09/2026 lúc 10:30:00`).
   - Nút hành động chính: Nút **"Quay lại danh sách"** (Outline button kèm icon mũi tên sang trái).
2. **Lưới thẻ thông tin tổng quan (2-Column Info Grid):**
   - **Thẻ 1: Thông tin người nhận:**
     - Icon nhận diện: User icon.
     - Khách hàng: Tên khách hàng (in đậm).
     - Số điện thoại: Link bấm gọi nhanh `tel:...` màu Primary.
     - Địa chỉ giao hàng: Địa chỉ đầy đủ, ngắt dòng tự nhiên, dễ đọc.
     - Ghi chú giao hàng: Chữ nghiêng màu dịu, nếu không có ghi chú thì hiển thị *"Không có ghi chú"*.
   - **Thẻ 2: Thông tin thanh toán & thời gian:**
     - Icon nhận diện: Credit card icon.
     - Phương thức: Hiển thị tên phương thức rõ ràng (ví dụ: `COD (Thanh toán khi nhận hàng)`).
     - Trạng thái thanh toán: Pill Badge rõ ràng (`Đã thanh toán` - Xanh lá, `Chưa thanh toán` - Vàng cam).
     - Ngày tạo đơn: Định dạng chuẩn Việt Nam `dd/MM/yyyy HH:mm:ss`.
     - Cập nhật lần cuối: Hiển thị thời điểm sửa đổi gần nhất hoặc `-`.
3. **Bảng danh sách mặt hàng (Order Items Card):**
   - Tiêu đề bảng: **Danh sách sản phẩm đã đặt** kèm nhãn tổng số lượng: `Tổng cộng: X món`.
   - Cột 1: **STT** (Căn giữa, màu xám nhẹ).
   - Cột 2: **Sản phẩm** (Tên sản phẩm in đậm, font rõ ràng).
   - Cột 3: **Mã sản phẩm** (Code badge bo góc, font monospace).
   - Cột 4: **Đơn giá** (Căn phải, định dạng tiền tệ VND `#,##0 đ`).
   - Cột 5: **Số lượng** (Căn giữa, in đậm).
   - Cột 6: **Thành tiền** (Căn phải, in đậm, màu chữ nổi bật).
   - **Table Footer (Tổng kết):** Dòng tổng kết tài chính với tổng số lượng sản phẩm và **Tổng tiền đơn hàng** (Font to `1.25rem`, màu Primary đậm, in đậm).

#### B. Bảng màu trạng thái (Status Badge - WCAG AA Compliant):
| Trạng thái | Tên hiển thị | Màu nền (Pastel) | Màu chữ (Đậm) | Ý nghĩa tâm lý |
| :--- | :--- | :--- | :--- | :--- |
| `PENDING` | Chờ xác nhận | `#FEF3C7` (Amber 100) | `#92400E` (Amber 800) | Đơn mới, cần xác nhận ngay |
| `PROCESSING` | Đang xử lý | `#E0E7FF` (Indigo 100) | `#3730A3` (Indigo 800) | Đang đóng gói, chuẩn bị hàng |
| `SHIPPED` | Đang giao hàng | `#F3E8FF` (Purple 100) | `#6B21A8` (Purple 800) | Hàng đang luân chuyển bên ngoài |
| `DELIVERED` | Đã giao hàng | `#D1FAE5` (Emerald 100) | `#065F46` (Emerald 800) | Giao hàng hoàn tất thành công |
| `CANCELLED` | Đã hủy | `#FEE2E2` (Red 100) | `#991B1B` (Red 800) | Đơn hàng đã bị hủy bỏ |

---

### 4. ♿ Accessibility (a11y) & Responsive Grid

#### A. Khả năng tiếp cận (Accessibility - WCAG 2.1 AA):
- Tất cả các icon SVG đều có thuộc tính `aria-hidden="true"`.
- Nút "Quay lại danh sách" có `aria-label="Quay lại danh sách đơn hàng"`.
- Số điện thoại khách hàng dùng thẻ `<a href="tel:..." aria-label="Gọi điện cho khách hàng {customerName}">` hỗ trợ công nghệ trợ năng.
- Bảng HTML tuân thủ ngữ nghĩa cấu trúc: `<caption>`, `<th scope="col">`, `<tbody>`, `<tfoot>`.
- Độ tương phản chữ/nền đảm bảo $\ge 4.5:1$ trên mọi độ phân giải.
- Hỗ trợ đầy đủ phím `Tab` duyệt theo thứ tự logic DOM và hiển thị đường viền `focus-visible`.

#### B. Thích ứng đa thiết bị (Responsive Degradation):
- **Desktop ($\ge$ 1024px):** Lưới thông tin 2 cột (`grid-template-columns: 1fr 1fr`), bảng sản phẩm hiển thị đầy đủ 6 cột.
- **Tablet (768px - 1023px):** Thẻ thông tin tự động co giãn (`minmax(300px, 1fr)`). Bảng sản phẩm nằm trong container `.table-responsive` với thanh cuộn ngang mượt mà.
- **Mobile (< 768px):** Thẻ thông tin xếp chồng 1 cột dọc (`1fr`). Cột tiêu đề header tự động xuống dòng gọn gàng. Nút "Quay lại" kéo rộng vừa vặn tầm ngón tay (`min-height: 44px`).

---

### 5. ✍️ UX Writing & Microcopy (Thân thiện, rõ ràng, không thuật ngữ)
- **Tiêu đề đơn:** *"Đơn hàng #ORD26000001"* (ngắn gọn, trực diện).
- **Ghi chú đơn hàng rỗng:** *"Không có ghi chú nào từ khách hàng."* (thay vì chữ `null` hoặc để trống).
- **Cập nhật cuối:** *"Chưa có cập nhật"* nếu `updatedAt` rỗng.
- **Empty State danh sách món:** *"Đơn hàng này hiện không có sản phẩm nào được ghi nhận."*
- **Flash Message khi lỗi không tìm thấy đơn:** *"Đơn hàng yêu cầu không tồn tại hoặc đã được gỡ bỏ khỏi hệ thống."*
- **Nút điều hướng:** *"Quay lại danh sách"* kèm icon mũi tên chỉ hướng rõ ràng.

---

### 6. ⚙️ Thymeleaf Implementation Notes
- Sử dụng layout kế thừa: `layout/admin-layout.html`.
- Định dạng ngày giờ chuẩn: `${#temporals.format(order.orderDate, 'dd/MM/yyyy HH:mm:ss')}`.
- Định dạng tiền tệ VND chuẩn: `${#numbers.formatDecimal(order.totalAmount, 0, 'COMMA', 0, 'POINT')} + ' đ'`.
- Kiểm tra danh sách sản phẩm: `${#lists.isEmpty(order.items)}`.
- Render màu sắc badge tự động qua class CSS: `status-pending`, `status-processing`, `status-shipped`, `status-delivered`, `status-cancelled`.
