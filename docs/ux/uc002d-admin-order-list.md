# 🎨 UX/UI Blueprint: uc002d-admin-order-list (Quản lý đơn hàng trực tuyến)

Tài liệu thiết kế trải nghiệm người dùng (UX Blueprint) đạt chuẩn Senior UX/UI cho giao diện danh sách đơn hàng dành cho Quản trị viên trong hệ thống bán mỹ phẩm.

---

### 1. 🎯 User Goal & Context
- **Đối tượng người dùng:** Quản trị viên bán hàng & Nhân viên vận hành kho (Admin / Order Processing Specialist).
- **Trạng thái tâm lý:** Cần tốc độ cao, thường xuyên quét mắt để xác nhận đơn hàng mới (`PENDING`), kiểm tra đơn hàng đang giao (`SHIPPED`) hoặc tra cứu nhanh đơn khi khách gọi hotline khiếu nại.
- **Mục tiêu cốt lõi trong 3 giây đầu tiên:**
  - Nhìn thấy ngay tổng số đơn hàng hiện có và các đơn hàng mới phát sinh nhất ở đầu bảng (`createdAt DESC`).
  - Phân biệt tức thì trạng thái của từng đơn hàng thông qua hệ thống màu sắc (Color Psychology) của Status Badge mà không cần đọc từng chữ.
  - Nhanh chóng định vị được ô tìm kiếm mã đơn / số điện thoại khách hàng.

---

### 2. 🧠 "Don't Make Me Think" (Giảm tải nhận thức & Tối ưu luồng thao tác)
- **Predictive Actions (Dự đoán hành động):**
  - **Sắp xếp tự nhiên:** Mặc định luôn đưa đơn hàng mới đặt nhất lên đầu danh sách (`createdAt DESC`).
  - **Duy trì bộ lọc (State Persistence):** Sau khi bấm "Lọc", toàn bộ thông tin (từ khóa, trạng thái đã chọn, khoảng ngày) được giữ nguyên trên form để người dùng biết mình đang xem dữ liệu trong phạm vi nào.
  - **Clickable Hotspots:** Click trực tiếp vào Mã đơn hàng (`orderCode`) hoặc nút icon "Xem chi tiết" đều đưa người dùng đến trang chi tiết đơn hàng (`/admin/orders/{id}`).
- **Error Prevention (Phòng ngừa lỗi chủ động):**
  - **Ràng buộc ngày tháng:** Khống chế trường `toDate` không được vượt quá ngày hiện tại (`max = today`).
  - **Smart Date Validation Feedback:** Khi phát hiện `fromDate > toDate`, giữ nguyên form, hiển thị thông báo lỗi bằng tiếng Việt rõ ràng, kèm nút "Đặt lại bộ lọc" giúp khôi phục dữ liệu ban đầu chỉ với 1 click.
  - **Tránh nhầm lẫn tìm kiếm:** Tự động cắt tỉa khoảng trắng thừa (`trim()`) ở ô tìm kiếm trước khi gửi request.

---

### 3. 📐 Visual & Layout Strategy

#### A. Bố cục tổng thể (Visual Flow & Information Architecture):
1. **Breadcrumb & Header:**
   - Điều hướng: `Trang chủ / Quản lý bán hàng / Đơn hàng`.
   - Tiêu đề nổi bật: **Quản lý đơn hàng** kèm badge số đếm tổng đơn: `Tổng cộng: 24 đơn hàng`.
2. **Bộ lọc đa tiêu chí (Comprehensive Toolbar):**
   - Ô tìm kiếm từ khóa với icon kính lúp: Placeholder *"Tìm theo mã đơn, tên hoặc số điện thoại khách hàng..."*.
   - Dropdown chọn Trạng thái: Tất cả trạng thái, Chờ xác nhận, Đang xử lý, Đang giao hàng, Đã giao hàng, Đã hủy.
   - Bộ chọn khoảng ngày: `Từ ngày` - `Đến ngày`.
   - Bộ nút thao tác: Nút **"Lọc dữ liệu"** (Primary button) và nút **"Đặt lại"** (Secondary button).
   - Bộ chọn số lượng hiển thị (`Page Size`): 10, 20, 50 dòng/trang.
3. **Bảng dữ liệu đơn hàng (Data Grid):**
   - Cột 1: **Mã đơn hàng** (Font monospace, màu Primary, in đậm, kèm link điều hướng).
   - Cột 2: **Khách hàng** (Tên khách in đậm, số điện thoại hiển thị nhỏ hơn màu xám ở dòng dưới).
   - Cột 3: **Ngày đặt hàng** (Định dạng chuẩn Việt Nam `dd/MM/yyyy HH:mm`).
   - Cột 4: **Trạng thái** (Pill Badge với icon & màu sắc chuẩn WCAG).
   - Cột 5: **Số lượng SP** (Căn giữa, kèm nhãn "sp").
   - Cột 6: **Tổng tiền** (Căn phải, in đậm, định dạng tiền tệ VND với dấu chấm phân cách `#,##0 ₫`).
   - Cột 7: **Thao tác** (Nút icon xem chi tiết có focus state & tooltip).
4. **Empty State (Khi không có dữ liệu):**
   - Icon hộp rỗng tinh gọn, tiêu đề *"Không tìm thấy đơn hàng nào phù hợp"*, mô tả *"Hãy thử điều chỉnh từ khóa tìm kiếm hoặc đặt lại bộ lọc"* kèm nút CTA *"Xóa bộ lọc"*.
5. **Thanh phân trang (Pagination Bar):**
   - Bên trái: *"Hiển thị X - Y trên tổng số Z đơn hàng"*.
   - Bên phải: Các nút Trang trước, danh sách trang (tối đa 5 trang kèm dấu `...`), Trang kế tiếp.

#### B. Bảng màu trạng thái (Status Badge Color Psychology - WCAG AA Compliant):
| Trạng thái | Tên hiển thị | Màu nền (Pastel) | Màu chữ (Đậm) | Ý nghĩa tâm lý |
| :--- | :--- | :--- | :--- | :--- |
| `PENDING` | Chờ xác nhận | `#FEF3C7` (Amber 100) | `#92400E` (Amber 800) | Cảnh báo / Cần hành động ngay |
| `PROCESSING` | Đang xử lý | `#E0E7FF` (Indigo 100) | `#3730A3` (Indigo 800) | Đang thực thi / Đóng gói |
| `SHIPPED` | Đang giao hàng | `#F3E8FF` (Purple 100) | `#6B21A8` (Purple 800) | Đang luân chuyển bên ngoài |
| `DELIVERED` | Đã giao hàng | `#D1FAE5` (Emerald 100) | `#065F46` (Emerald 800) | Hoàn tất an toàn / Thành công |
| `CANCELLED` | Đã hủy | `#FEE2E2` (Red 100) | `#991B1B` (Red 800) | Đã kết thúc / Hủy bỏ |

---

### 4. ♿ Accessibility (a11y) & Responsive Grid

#### A. Khả năng tiếp cận (Accessibility - WCAG 2.1 AA):
- Tất cả các nút bấm icon (ví dụ nút Xem chi tiết, nút Xóa bộ lọc) đều có thuộc tính `aria-label="Xem chi tiết đơn hàng {orderCode}"` và `title`.
- Độ tương phản màu chữ so với nền bảng và badge đều đạt tỷ lệ $\ge 4.5:1$.
- Bảng HTML sử dụng đúng thẻ ngữ nghĩa: `<caption>`, `<thead scope="col">`, `<tbody>`.
- Hỗ trợ phím `Tab` để di chuyển mượt mà qua các ô input, nút bấm và liên kết hàng với `focus-visible` outline rõ ràng.

#### B. Responsive Degradation (Thích ứng đa thiết bị):
- **Desktop ($\ge$ 1024px):** Bảng hiển thị đầy đủ 7 cột dạng Data Grid tối ưu diện tích.
- **Tablet (768px - 1023px):** Thẻ bao bọc `table-responsive` kích hoạt thanh cuộn ngang mượt mà, cố định chiều rộng cột tối thiểu. Cột Khách hàng gom nhóm tên + SĐT thành 1 cell dọc.
- **Mobile (< 768px):** Bộ lọc tự động xếp chồng (Stack 1 cột), bảng hỗ trợ vuốt chạm (`touch swipe`), nút bấm có kích thước tối thiểu `44x44px` cho ngón tay.

---

### 5. ✍️ UX Writing & Microcopy (Giao tiếp thân thiện, không dùng thuật ngữ kỹ thuật)
- **Thông báo lỗi khoảng ngày:** *"Khoảng thời gian không hợp lệ: Ngày bắt đầu không thể sau ngày kết thúc. Vui lòng chọn lại."*
- **Thông báo lỗi từ khóa:** *"Từ khóa tìm kiếm quá dài (tối đa 100 ký tự). Vui lòng rút gọn từ khóa."*
- **Thông báo không tìm thấy đơn:** *"Không tìm thấy đơn hàng nào khớp với điều kiện tìm kiếm. Hãy thử xóa bộ lọc để xem lại tất cả."*
- **Flash Message khi đơn không tồn tại:** *"Đơn hàng yêu cầu không tồn tại hoặc đã được chuyển vào lưu trữ."*

---

### 6. ⚙️ Thymeleaf Implementation Notes
- Sử dụng Layout kế thừa: `layout/admin-layout.html`.
- Định dạng ngày giờ: `#temporals.format(order.orderDate, 'dd/MM/yyyy HH:mm')`.
- Định dạng tiền tệ: `#numbers.formatDecimal(order.totalAmount, 0, 'POINT', 0, 'COMMA') + ' ₫'`.
- Giữ trạng thái form qua thuộc tính `th:value` và `th:selected` liên kết với model `filter`.
