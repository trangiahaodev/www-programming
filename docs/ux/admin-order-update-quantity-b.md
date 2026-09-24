# 🎨 UX/UI Blueprint: admin-order-update-quantity-b (Cập nhật số lượng mặt hàng)

Tài liệu thiết kế trải nghiệm người dùng (UX Blueprint) đạt chuẩn Senior UX/UI (Google UX Design Certified) cho tính năng **Quản trị viên cập nhật số lượng mặt hàng trong đơn hàng trực tuyến** (`admin-order-update-quantity-b` / `uc002b-admin-order-update-quantity`) trong hệ thống bán mỹ phẩm trực tuyến Cosmetic Shop.

---

### 1. 🎯 User Goal & Context

- **Đối tượng người dùng:** Quản trị viên bán hàng, Chuyên viên Chăm sóc khách hàng (CSKH), Nhân viên kiểm soát đơn và Trưởng kho vận hành (Sales Admin & Warehouse Operator).
- **Trạng thái tâm lý:**
  - **Khẩn trương & Cần độ chính xác tuyệt đối:** Đang nghe cuộc gọi hotline từ khách xin đổi số lượng (mua thêm sản phẩm hoặc giảm số lượng do đặt nhầm); hoặc kiểm kho thấy thiếu hàng cần điều chỉnh trước khi xuất đơn.
  - **Sợ sai lệch tiền bạc:** Lo lắng việc cập nhật số lượng thủ công có thể quên tính lại thành tiền hoặc tổng tiền đơn hàng dẫn đến thu thiếu/thu thừa tiền của khách.
  - **Ngại thao tác rườm rà:** Muốn sửa ngay trên giao diện chi tiết đơn hàng hiện có mà không phải chuyển trang hay nhập lại mã sản phẩm.
- **Mục tiêu cốt lõi trong 3 giây đầu tiên:**
  - **Quyền được sửa (Editability Status):** Nhận biết ngay đơn hàng có ở trạng thái cho phép sửa hay không (`PENDING` hoặc `PROCESSING` thì mở khóa các ô số lượng; `SHIPPED`, `DELIVERED`, `CANCELLED` thì bị khóa và hiển thị lý do minh bạch).
  - **Bộ điều khiển số lượng (Quantity Stepper):** Thấy ngay các nút bấm `[ - ]` `[ Số lượng ]` `[ + ]` trực quan tại cột Số lượng của từng món hàng.
  - **Phản hồi tính toán thời gian thực (Live Financial Preview):** Khi thay đổi số lượng, Thành tiền của món và Tổng tiền đơn hàng lập tức cập nhật nhảy số trước mắt mà không cần tải lại trang.
  - **Nút hành động rõ ràng:** Cặp nút **"Lưu thay đổi"** (Primary Action) và **"Đặt lại ban đầu"** (Secondary Action) nổi bật, hiển thị số món đã chỉnh sửa.

---

### 2. 🧠 "Don't Make Me Think" (Giảm tải nhận thức & Phòng chống lỗi)

- **Predictive Actions & Tự động hóa trải nghiệm:**
  - **Stepper Control thông minh:** Hỗ trợ cả 3 cách chỉnh số lượng:
    1. Nhấp nút `+` / `-` để tăng/giảm 1 đơn vị.
    2. Nhập trực tiếp bàn phím con số mong muốn.
    3. Dùng phím mũi tên Lên/Xuống (ArrowUp / ArrowDown) khi đang focus ô input.
  - **Tự động tính tiền Client-Side:** JavaScript tính tức thì `subtotal = unitPrice * quantity` và `totalAmount = sum(all subtotal)`, kèm hiệu ứng highlight màu xanh nhẹ (Pulse effect) tại các con số vừa biến động.
  - **Trạng thái kích hoạt nút thông minh (Dirty State Detection):**
    - Mặc định khi chưa có thay đổi nào, nút "Lưu thay đổi" ở trạng thái `disabled` (hoặc hiển thị mờ) để tránh submit request thừa lên máy chủ.
    - Khi có ít nhất một mặt hàng đổi số lượng, nút "Lưu thay đổi" tự động sáng đèn và hiển thị kèm huy hiệu: *"Lưu thay đổi (1 mặt hàng)"*.
  - **Nút "Đặt lại ban đầu" (One-Click Reset):** Khôi phục toàn bộ số lượng ban đầu của tất cả các dòng chỉ với một cú nhấp chuột mà không cần tải lại toàn trang.

- **Error Prevention (Phòng ngừa lỗi từ sớm):**
  - **Ràng buộc giá trị nhập (Input Guarding):**
    - Chặn hoàn toàn ký tự chữ, số thập phân, ký tự đặc biệt (`e`, `+`, `-`, `.`).
    - Giới hạn cứng `min="1"` và `max="999"`. Nút `[ - ]` tự động disable khi số lượng đạt mức tối thiểu bằng 1 (tránh trường hợp giảm về 0 làm mất món).
  - **Cảnh báo trạng thái khóa đơn (Locked State Alert):**
    - Nếu đơn hàng đã `DELIVERED`, `SHIPPED` hoặc `CANCELLED`, toàn bộ ô input bị vô hiệu hóa (`disabled="disabled"`).
    - Hiển thị Banner cảnh báo mềm màu hổ phách: *"Đơn hàng đang ở trạng thái [Đã giao hàng]. Quy định nghiệp vụ không cho phép thay đổi số lượng mặt hàng."*
  - **Xác nhận thay đổi tài chính quan trọng (Financial Confirmation Modal / Toast):**
    - Trước khi submit form, hiển thị bảng tóm tắt nhanh: *"Tổng tiền đơn hàng sẽ thay đổi từ 850.000 đ thành 1.100.000 đ (+250.000 đ). Bạn có chắc chắn muốn lưu?"*.

---

### 3. 📐 Visual & Layout Strategy

#### A. Bố cục trực quan trên giao diện Chi tiết đơn hàng:
1. **Thanh Toolbar quản lý thao tác (Ngay phía trên bảng sản phẩm):**
   - Bên trái: Tiêu đề bảng **"Danh sách sản phẩm đã đặt"** kèm badge tổng số lượng món.
   - Bên phải: Cụm nút bấm tương tác:
     - Nút **"Đặt lại ban đầu"** (Nút phụ Ghost/Outline, icon khôi phục xoay tròn).
     - Nút **"Lưu thay đổi"** (Nút chính Solid Indigo `#4F46E5`, icon đĩa mềm / checkmark, có badge đếm số món đã sửa).
2. **Cột "Số lượng" được cải tiến thành cụm Stepper:**
   - Cấu trúc: `[ - ]` nút tròn mềm `|` ô input số lượng căn giữa `|` `[ + ]` nút tròn mềm.
   - Khi focus vào input: Hiển thị viền xanh Primary bo góc `0.5rem`, hiệu ứng đổ bóng mờ `ring-2 ring-indigo-500/20`.
3. **Cột "Thành tiền" & "Tổng tiền":**
   - Định dạng tiền tệ VND phân cách hàng nghìn rõ ràng: `#,##0 đ`.
   - Khi giá trị thay đổi so với ban đầu: Font chữ chuyển sang màu xanh ngọc (`Emerald 600`) kèm nhãn chênh lệch nhỏ phía dưới (ví dụ: `+200.000 đ`).
4. **Footer Bảng tổng kết tài chính (Table Summary Footer):**
   - Dòng tổng: Hiển thị tổng số lượng sản phẩm mới và **Tổng tiền thanh toán mới** cỡ chữ `1.25rem` in đậm.

#### B. Bảng quy chuẩn màu sắc tương tác:
| Thành phần | Trạng thái bình thường | Trạng thái Hover / Focus | Trạng thái Disabled | Ý nghĩa UX |
| :--- | :--- | :--- | :--- | :--- |
| **Nút Stepper `+` / `-`** | Nền `#F1F5F9`, chữ `#475569` | Nền `#E2E8F0`, chữ `#0F172A` | Nền `#F8FAFC`, chữ `#CBD5E1`, cursor `not-allowed` | Phím bấm nảy số, giảm tải áp lực thị giác |
| **Ô Input số lượng** | Viền `#CBD5E1`, nền trắng | Viền `#6366F1`, đổ bóng Indigo | Nền `#F1F5F9`, chữ `#94A3B8` | Báo hiệu sẵn sàng nhận dữ liệu |
| **Nút "Lưu thay đổi"** | Nền `#4F46E5`, chữ trắng | Nền `#4338CA`, nâng nhẹ | Nền `#E2E8F0`, chữ `#94A3B8` | Hành động chính, chỉ kích hoạt khi có thay đổi |
| **Nút "Đặt lại"** | Viền `#E2E8F0`, chữ `#64748B` | Nền `#F8FAFC`, chữ `#1E293B` | Ẩn khi không có thay đổi | Lối thoát an toàn khi nhập sai |

---

### 4. ♿ Accessibility (a11y) & Responsive Grid

#### A. Khả năng tiếp cận (Accessibility - WCAG 2.1 AA Compliant):
- **Screen Reader Support:**
  - Nút giảm số lượng: `aria-label="Giảm 1 sản phẩm {productName}"`.
  - Nút tăng số lượng: `aria-label="Tăng 1 sản phẩm {productName}"`.
  - Ô input số lượng: `aria-label="Số lượng sản phẩm {productName}"` kèm thuộc tính `role="spinbutton"`, `aria-valuemin="1"`, `aria-valuemax="999"`.
  - Cột Thành tiền và Tổng tiền có thuộc tính `aria-live="polite"` để phần mềm đọc màn hình tự động thông báo giá trị mới khi số lượng thay đổi.
- **Điều hướng bàn phím (Keyboard Flow):**
  - Người dùng có thể nhấn `Tab` để di chuyển tuần tự qua từng ô số lượng.
  - Phím `Enter` trong ô input kích hoạt lưu thay đổi nhanh.
  - Phím `Esc` khôi phục lại giá trị của dòng hiện tại.

#### B. Thích ứng đa thiết bị (Responsive Degradation):
- **Màn hình Desktop ($\ge$ 1024px):** Hiển thị đầy đủ bảng dữ liệu 6 cột, cụm Stepper căn giữa với độ rộng tiêu chuẩn 130px.
- **Màn hình Tablet (768px - 1023px):** Bảng có thanh cuộn ngang mượt mà (`.table-responsive`), cụm Stepper giữ kích thước phím bấm tối thiểu $36 \times 36\text{px}$.
- **Màn hình Mobile (< 768px):**
  - Chuyển đổi hiển thị từ Bảng sang **Danh thiếp sản phẩm (Product Item Cards)**. Mỗi sản phẩm là 1 thẻ riêng biệt: Tên sản phẩm trên cùng, bên dưới là Đơn giá và Stepper căn lề phải.
  - **Sticky Bottom Action Bar:** Nút "Lưu thay đổi" và "Đặt lại" được ghim cố định ở đáy màn hình điện thoại (Sticky bottom bar) giúp ngón tay cái dễ dàng chạm tới mà không cần cuộn ngược lên đầu trang.

---

### 5. ✍️ UX Writing & Microcopy (Giao tiếp tự nhiên, không thuật ngữ máy tính)

- **Thông báo thành công (Flash Success):**
  - *"Đã cập nhật số lượng và tính lại tổng tiền đơn hàng thành công!"* (Ngắn gọn, mang tính khẳng định).
- **Thông báo lỗi xác thực số lượng:**
  - *"Số lượng sản phẩm phải từ 1 đến 999 món. Vui lòng kiểm tra lại."* (Tránh dùng từ kỹ thuật như "JSR-380 validation failure").
- **Thông báo trạng thái đơn cấm sửa:**
  - *"Đơn hàng này đang ở trạng thái [Đã giao hàng] nên không thể chỉnh sửa số lượng sản phẩm."*
- **Nhãn nút bấm (Action Microcopy):**
  - `"Lưu thay đổi"` thay vì `"Cập nhật"` hay `"Submit"`.
  - `"Đặt lại ban đầu"` thay vì `"Cancel"` hay `"Reset"`.
- **Tooltip hướng dẫn:**
  - `"Nhấp để tăng số lượng"` / `"Nhấp để giảm số lượng (Tối thiểu 1 món)"`.

---

### 6. ⚙️ Thymeleaf Implementation Notes

- **Cấu trúc Form Thymeleaf:**
  ```html
  <form th:action="@{/admin/orders/{id}/items(id=${order.id})}" 
        method="post" 
        id="updateQuantityForm"
        th:object="${updateRequest}">
      <input type="hidden" name="orderId" th:value="${order.id}">
      ...
  </form>
  ```
- **Binding động từng dòng mặt hàng:**
  ```html
  <tr th:each="item, stat : ${order.items}" th:attr="data-item-id=${item.id}, data-unit-price=${item.unitPrice}">
      <input type="hidden" 
             th:name="|items[${stat.index}].orderItemId|" 
             th:value="${item.id}" />
      <div class="quantity-stepper">
          <button type="button" class="btn-stepper btn-decrement" aria-label="Giảm số lượng">-</button>
          <input type="number" 
                 class="input-quantity" 
                 th:name="|items[${stat.index}].quantity|" 
                 th:value="${item.quantity}" 
                 min="1" max="999" />
          <button type="button" class="btn-stepper btn-increment" aria-label="Tăng số lượng">+</button>
      </div>
  </tr>
  ```
- **Xử lý hiển thị thông báo phản hồi (Flash Alert Containers):**
  - Nhận diện tự động `th:if="${successMessage}"` và `th:if="${errorMessage}"` hiển thị Toast / Banner nổi bật trên đầu trang chi tiết.
- **Khối Script bổ trợ (Progressive Enhancement):**
  - JavaScript lắng nghe sự kiện `input` và `click` trên stepper để cập nhật thành tiền, tổng tiền và kích hoạt nút Submit theo thời gian thực mà không phụ thuộc vào tải lại trang.

---

**→ Bản nháp UX/UI này đã đủ 'mượt' chưa? (yes / thêm ý tưởng)**
