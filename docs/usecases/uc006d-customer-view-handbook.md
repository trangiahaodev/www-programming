# Đặc tả Use Case: Khách hàng tra cứu Cẩm nang Skincare 7 Bước

**Mã Use Case:** `uc006d-customer-view-handbook`  
**Tên Use Case:** Khách hàng tra cứu Cẩm nang Skincare 7 Bước Chuẩn Hàn & Test Chẩn đoán loại da  
**Actor chính:** Khách hàng (Customer), Khách vãng lai (Guest)  
**Phân hệ:** Customer Storefront (Cẩm nang Skincare PinkyCloud)

---

## 1. Mô tả tóm tắt
Cung cấp hướng dẫn toàn diện về quy trình dưỡng da 7 bước khoa học theo chuẩn Hàn Quốc, tích hợp công cụ trắc nghiệm tương tác giúp khách hàng tự chẩn đoán loại da cá nhân (Da dầu, Da khô, Da hỗn hợp, Da nhạy cảm), từ đó đưa ra khuyến nghị routine và các sản phẩm mỹ phẩm phù hợp nhất với tình trạng da thực tế.

---

## 2. Tiền điều kiện (Pre-conditions)
- Ứng dụng PinkyCloud đang hoạt động.
- Dữ liệu 7 bước Skincare và liên kết danh mục mỹ phẩm đã sẵn sàng.

---

## 3. Hậu điều kiện (Post-conditions)
- Khách hàng hiểu rõ nguyên lý chăm sóc da và nhận được kết quả chẩn đoán loại da cá nhân.
- Khách hàng dễ dàng lựa chọn mỹ phẩm phù hợp với chu trình dưỡng da của bản thân.

---

## 4. Luồng sự kiện chính (Basic Flow)
1. Khách hàng truy cập đường dẫn `/cam-nang` từ thanh điều hướng hoặc banner trang chủ.
2. Hệ thống hiển thị giao diện Cẩm nang Skincare chuyên biệt gồm:
   - Banner Hero giới thiệu Cẩm nang khoa học.
   - Thanh điều hướng nhanh giữa các chương (Chương 1: Chẩn đoán da, Chương 2: Routine sáng/tối, Chương 3: Chi tiết 7 bước, Chương 4: Tra cứu thành phần).
   - Công cụ Interactive Quiz: 4 câu hỏi trắc nghiệm kiểm tra độ nhờn, phản ứng thời tiết, lỗ chân lông và độ nhạy cảm của da.
   - Thẻ hiển thị chi tiết từng bước trong quy trình 7 bước (Tẩy trang ➔ Sữa rửa mặt ➔ Toner ➔ Tinh chất/Serum ➔ Kem mắt ➔ Kem dưỡng ẩm ➔ Kem chống nắng).
   - Danh sách mỹ phẩm tiêu biểu được kiểm nghiệm khuyên dùng cho từng bước.
3. Khách hàng thực hiện bài test chẩn đoán da và nhận kết quả phân loại da ngay lập tức.
4. Khách hàng nhấp vào sản phẩm khuyên dùng để xem thông tin chi tiết và tiến hành mua hàng.

---

## 5. Luồng rẽ nhánh (Alternative Flows)
- **A1. Tra cứu nhanh một bước cụ thể:** Khách hàng nhấp vào thẻ bước (ví dụ: *Bước 4: Serum phục hồi*). Hệ thống cuộn mượt đến thông tin chuyên sâu của bước đó kèm lưu ý cách thoa và thời gian giãn cách giữa các lớp skincare.
- **A2. Khách hàng làm lại bài test:** Khách hàng có thể nhấn nút "Làm lại bài kiểm tra" bất kỳ lúc nào để chọn lại đáp án.

---

## 6. Yêu cầu phi chức năng (Non-functional Requirements)
- **Giao diện:** Tương thích responsive trên Mobile, Tablet, Desktop; sử dụng icon vector SVG chuyên nghiệp thay vì icon generic; hiệu ứng chuyển động mượt mà.
- **Tính toán Client-side:** Bài test tính toán điểm số tức thì không cần tải lại trang (Zero-latency interaction).
