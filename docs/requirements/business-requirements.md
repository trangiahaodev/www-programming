# ĐẶC TẢ NGHIỆP VỤ & BỘ QUY ĐỊNH VẬN HÀNH DOANH NGHIỆP
## HỆ THỐNG QUẢN LÝ VÀ BÁN MỸ PHẨM TRỰC TUYẾN PINKYCLOUD

---

## MỤC LỤC

1. [PHẦN 1: BỘ QUY ĐỊNH VẬN HÀNH DOANH NGHIỆP (INDUSTRY STANDARDS)](#phần-1-bộ-quy-định-vận-hành-doanh-nghiệp-industry-standards)  
   1.1. [Quy định Quản lý Giỏ hàng & Kiểm soát Tồn kho (Cart & Inventory Allocation)](#11-quy-định-quản-lý-giỏ-hàng--kiểm-soát-tồn-kho-cart--inventory-allocation)  
   1.2. [Quy định Khuyến mãi & Thứ tự Áp dụng Chiết khấu (Promotion Stacking Rules)](#12-quy-định-khuyến-mãi--thứ-tự-áp-dụng-chiết-khấu-promotion-stacking-rules)  
   1.3. [Quy định Hạng Thành viên & Điểm thưởng Tích lũy (Loyalty Program & Reward Points)](#13-quy-định-hạng-thành-viên--điểm-thưởng-tích-lũy-loyalty-program--reward-points)  
   1.4. [Quy định Vòng đời Đơn hàng, Hủy đơn & Hoàn trả Tài nguyên (Order Lifecycle, Refund & Restock)](#14-quy-định-vòng-đời-đơn-hàng-hủy-đơn--hoàn-trả-tài-nguyên-order-lifecycle-refund--restock)  
   1.5. [Quy định Đổi trả Hàng hóa & Chính sách Hậu mãi (Return & Exchange Policy)](#15-quy-định-đổi-trả-hàng-hóa--chính-sách-hậu-mãi-return--exchange-policy)  

2. [PHẦN 2: 🎯 BỘ CÂU HỎI "XOÁY" BẢO VỆ ĐỒ ÁN (DEFENSE Q&A)](#phần-2--bộ-câu-hỏi-xoáy-bảo-vệ-đồ-án-defense-qa)  
   - [Câu hỏi 1: Xử lý Tranh chấp Tồn kho & Đặt hàng Đồng thời (Race Condition)](#câu-hỏi-1-xử-lý-tranh-chấp-tồn-kho--đặt-hàng-đồng-thời-race-condition)  
   - [Câu hỏi 2: Chống Trục lợi Hủy đơn & Lỗ hổng Cày Điểm thưởng / Voucher](#câu-hỏi-2-chống-trục-lợi-hủy-đơn--lỗ-hổng-cày-điểm-thưởng--voucher)  
   - [Câu hỏi 3: Kiểm soát Xung đột Cộng dồn Khuyến mãi (Promotion Stacking Abuse)](#câu-hỏi-3-kiểm-soát-xung-đột-cộng-dồn-khuyến-mãi-promotion-stacking-abuse)  
   - [Câu hỏi 4: Điều chỉnh Đơn hàng khi đang Chuẩn bị & Rủi ro Lệch Tồn kho Thực xuất](#câu-hỏi-4-điều-chỉnh-đơn-hàng-khi-đang-chuẩn-bị--rủi-ro-lệch-tồn-kho-thực-xuất)  
   - [Câu hỏi 5: Xử lý Khiếu nại Hàng Hư hỏng trong Quá trình Vận chuyển & Quy trình Đổi trả](#câu-hỏi-5-xử-lý-khiếu-nại-hàng-hư-hỏng-trong-quá-trình-vận-chuyển--quy-trình-đổi-trả)  

---

# PHẦN 1: BỘ QUY ĐỊNH VẬN HÀNH DOANH NGHIỆP (INDUSTRY STANDARDS)

Toàn bộ các quy tắc dưới đây được xây dựng dựa trên tiêu chuẩn vận hành thương mại điện tử ngành bán lẻ mỹ phẩm, hướng tới mục tiêu tối ưu hóa trải nghiệm khách hàng, bảo toàn biên lợi nhuận của doanh nghiệp và triệt tiêu các rủi ro gian lận hoặc xung đột dữ liệu vận hành.

---

## 1.1. Quy định Quản lý Giỏ hàng & Kiểm soát Tồn kho (Cart & Inventory Allocation)

### Điều 1. Giới hạn số lượng mua tối đa (Anti-Scalping Policy)
1. Để chống hành vi gom hàng đầu cơ, mua đi bán lại đối với các sản phẩm mỹ phẩm "hot" hoặc các chương trình khuyến mãi sâu, cửa hàng áp dụng hạn mức mua sắm:
   - Một khách hàng trong một lần đặt hàng chỉ được mua tối đa **10 đơn vị sản phẩm** cho cùng một mã hàng.
   - Trường hợp khách hàng cố tình vượt quá số lượng 10 sản phẩm trong giỏ hàng, hệ thống sẽ tự động ghim số lượng ở mức tối đa 10 và hiển thị thông báo nhắc nhở quy định mua sắm của cửa hàng.
2. Đối với khách hàng có nhu cầu mua sỉ (số lượng lớn hơn 10 sản phẩm/mã), khách hàng bắt buộc phải liên hệ trực tiếp với bộ phận kinh doanh để ký hợp đồng đại lý và áp dụng bảng giá phân phối sỉ riêng biệt.

### Điều 2. Cơ chế Giỏ hàng & Quy tắc Không Giữ Hàng Ảo (No Hold on Add-to-Cart)
1. Túi mua sắm (Giỏ hàng) của khách hàng đóng vai trò là danh sách ghi nhớ tạm thời các mặt hàng khách đang quan tâm.
2. **Quy tắc vàng:** Hành động thêm sản phẩm vào giỏ hàng **tuyệt đối KHÔNG làm trừ số lượng tồn kho** của sản phẩm.
   - *Lý do vận hành:* Nếu hệ thống giữ hàng (khóa kho) ngay khi khách thêm vào giỏ, các đối tượng xấu có thể mở hàng trăm phiên truy cập và thêm toàn bộ hàng vào giỏ mà không thanh toán, làm tê liệt hoạt động bán hàng của cửa hàng (kho báo hết hàng trong khi hàng thực tế vẫn nằm trên kệ).
3. Thông tin giỏ hàng được duy trì liên tục trong suốt phiên mua sắm của khách hàng. Khi khách hàng đóng trình duyệt hoặc hoàn tất đặt hàng, giỏ hàng sẽ tự động được giải phóng.

### Điều 3. Thời điểm Trừ kho Thực tế & Xử lý Tranh chấp Đồng thời
1. Số lượng tồn kho thực tế của sản phẩm chỉ được trừ chính thức tại thời điểm khách hàng nhấn nút **"Xác nhận Đặt hàng"** và đơn hàng được ghi nhận thành công vào hệ thống.
2. **Quy tắc giải quyết tranh chấp (First-Come, First-Served):**
   - Trường hợp mặt hàng chỉ còn lại 01 sản phẩm cuối cùng trên kệ, nhưng có từ 02 khách hàng trở lên cùng đang lưu sản phẩm này trong giỏ hàng:
     - Khách hàng nào hoàn tất thao tác "Xác nhận Đặt hàng" trước sẽ được hệ thống tạo đơn thành công và số lượng tồn kho giảm về 0.
     - Khách hàng bấm xác nhận sau sẽ nhận được thông báo: *"Sản phẩm [Tên sản phẩm] trong giỏ hàng của bạn vừa hết hàng. Vui lòng điều chỉnh giỏ hàng để tiếp tục thanh toán"*.

---

## 1.2. Quy định Khuyến mãi & Thứ tự Áp dụng Chiết khấu (Promotion Stacking Rules)

Nhằm đảm bảo tính hấp dẫn của các chương trình kích cầu mua sắm nhưng vẫn giữ vững biên độ an toàn lợi nhuận của cửa hàng, quy tắc áp dụng chiết khấu và khuyến mãi được phân tầng nghiêm ngặt.

### Điều 4. Thứ tự Ưu tiên Áp dụng Chiết khấu Đa tầng
Khi một đơn hàng có nhiều hình thức ưu đãi cùng tồn tại, hệ thống bắt buộc phải áp dụng theo đúng trình tự 4 bước sau:

$$\text{Tổng tiền sản phẩm theo Giá Niêm yết}$$
$$\downarrow \quad \text{(Bước 1: Trừ Giá giảm trực tiếp theo chương trình Flash Sale / Sản phẩm)}$$
$$\text{Giá trị hàng sau Giảm giá trực tiếp (A)}$$
$$\downarrow \quad \text{(Bước 2: Trừ Mã giảm giá đơn hàng - Voucher Code)}$$
$$\text{Giá trị hàng sau Voucher (B)}$$
$$\downarrow \quad \text{(Bước 3: Trừ Chiết khấu Tiêu điểm thưởng Thành viên)}$$
$$\text{Số tiền hàng Thực trả (C)}$$
$$\downarrow \quad \text{(Bước 4: Cộng Cước phí Vận chuyển tiêu chuẩn & Trừ Miễn phí Vận chuyển nếu đủ điều kiện)}$$
$$\textbf{TỔNG TIỀN THANH TOÁN CUỐI CÙNG}$$

### Điều 5. Quy tắc Loại trừ và Ngăn chặn Lạm dụng Khuyến mãi (Stacking Abuse)
1. **Quy tắc Voucher:** Trong một đơn hàng, khách hàng chỉ được phép sử dụng duy nhất **01 Mã giảm giá (Voucher Code)**. Tuyệt đối không cho phép cộng dồn 2 hoặc nhiều mã giảm giá cùng loại.
2. **Quy tắc tính % giảm giá của Voucher:** Giá trị giảm của Voucher theo tỷ lệ % (ví dụ: Voucher giảm 10%) sẽ được tính trên giá trị sau khi đã trừ giảm giá trực tiếp (Giá trị A), không tính trên giá niêm yết gốc ban đầu.
3. **Mức giảm tối đa của Voucher:** Mỗi mã giảm giá theo % bắt buộc phải có mức giảm trần tối đa (ví dụ: Giảm 15% tối đa 50.000 VNĐ) để tránh trường hợp các đơn hàng giá trị lớn làm thâm hụt ngân sách khuyến mãi.
4. **Giới hạn giá sàn thanh toán:** Sau khi áp dụng đầy đủ các tầng giảm giá và điểm thưởng, số tiền hàng thực trả (C) của một đơn hàng **không bao giờ được nhỏ hơn 0 VNĐ**. Cửa hàng không có chính sách hoàn tiền mặt thừa nếu tổng ưu đãi vượt quá giá trị đơn hàng.

---

## 1.3. Quy định Hạng Thành viên & Điểm thưởng Tích lũy (Loyalty Program & Reward Points)

### Điều 6. Cơ chế Tích lũy Điểm thưởng (Earning Points)
1. **Tỷ lệ quy đổi tích điểm:** Cứ mỗi **10.000 VNĐ** tiền hàng thực trả, khách hàng được tích lũy **1 Điểm thưởng** (tương đương tỷ lệ hoàn tiền 10% quy đổi ở lần mua tiếp theo).
2. **Quy tắc làm tròn:** Điểm thưởng được tính theo phần nguyên của phép chia:
   $$\text{Điểm tích lũy} = \left\lfloor \frac{\text{Số tiền hàng Thực trả (C)}}{10.000} \right\rfloor$$
3. **Điều kiện ghi nhận điểm tích lũy:**
   - Điểm thưởng **CHỈ được tính trên Số tiền hàng Thực trả (Giá trị C)** sau khi đã trừ toàn bộ mã giảm giá, voucher và điểm thưởng đã tiêu.
   - Tiền cước phí vận chuyển **không được tính** vào hạn mức tích điểm.
   - Điểm thưởng chỉ được cộng chính thức vào tài khoản thành viên sau khi đơn hàng chuyển sang trạng thái **"Hoàn tất" (Giao hàng thành công)**. Đơn hàng đang giao hoặc chưa thanh toán sẽ ở trạng thái "Điểm thưởng chờ duyệt".

### Điều 7. Cơ chế Tiêu Điểm thưởng (Redeeming Points)
1. **Tỷ lệ quy đổi tiêu điểm:** **1 Điểm thưởng = 1.000 VNĐ** giảm trừ trực tiếp vào tiền hàng.
2. **Điều kiện tiêu điểm:**
   - Tài khoản khách hàng phải có tối thiểu từ **10 điểm thưởng** trở lên mới được quyền sử dụng.
   - Khách hàng được tự do lựa chọn số điểm muốn tiêu (từ 10 điểm đến toàn bộ số điểm hiện có).
3. **Hạn mức trần tiêu điểm:** Số tiền giảm trừ từ điểm thưởng trong một đơn hàng **không được vượt quá 50% tổng giá trị tiền hàng sau voucher (Giá trị B)**. Quy định này nhằm đảm bảo khách hàng luôn phải chi trả một phần tiền mặt, tránh tình trạng tạo tài khoản ảo dồn điểm để nhận hàng miễn phí 100%.
4. Điểm thưởng không có giá trị quy đổi thành tiền mặt, không được chuyển nhượng giữa các tài khoản khách hàng khác nhau.

---

## 1.4. Quy định Vòng đời Đơn hàng, Hủy đơn & Hoàn trả Tài nguyên (Order Lifecycle, Refund & Restock)

### Điều 8. Vòng đời Trạng thái Đơn hàng
Mọi đơn hàng trong hệ thống PinkyCloud trải qua các trạng thái tuần tự và chặt chẽ:

```
[Chờ xác nhận] ──────► [Đang chuẩn bị hàng] ──────► [Đang giao hàng] ──────► [Hoàn tất]
       │                        │
       ▼                        ▼
    [Đã hủy]                 [Đã hủy]
 (Khách tự hủy/            (Quản trị hủy do
  Quản trị hủy)             hết hàng/sự cố)
```

1. **Chờ xác nhận:** Đơn hàng vừa được khách đặt thành công, bộ phận bán hàng đang kiểm tra thông tin địa chỉ và tồn kho.
2. **Đang chuẩn bị hàng:** Đơn hàng đã được duyệt, nhân viên kho đang in phiếu xuất và bốc hàng đóng gói vào hộp carton.
3. **Đang giao hàng:** Đơn hàng đã được bàn giao cho đơn vị bưu tá vận chuyển và đang trên đường chuyển phát tới khách.
4. **Hoàn tất:** Khách hàng đã nhận hàng và thanh toán tiền thành công (COD hoặc đã chuyển khoản).
5. **Đã hủy:** Đơn hàng bị chấm dứt do khách yêu cầu hủy, không liên lạc được với khách, hoặc các lý do bất khả kháng.

### Điều 9. Quyền hạn Hủy đơn & Quy tắc Hoàn trả Tài nguyên (Refund & Restock)
1. **Quyền hủy đơn của Khách hàng:** Khách hàng chỉ được quyền tự hủy đơn hàng trực tuyến khi đơn hàng đang ở trạng thái **"Chờ xác nhận"**. Khi đơn đã chuyển sang "Đang chuẩn bị hàng" hoặc "Đang giao hàng", khách muốn hủy bắt buộc phải gọi điện trực tiếp tới tổng đài hỗ trợ để nhân viên xử lý dừng giao.
2. **Quy tắc hoàn trả tài nguyên (Refund & Restock Rules):** Khi một đơn hàng bị hủy hợp lệ:
   - **Hoàn trả tồn kho (Restock):** 100% số lượng các mặt hàng trong đơn bị hủy lập tức được hoàn trả lại vào số lượng tồn kho khả dụng để phục vụ các khách hàng khác.
   - **Hoàn trả Điểm thưởng đã tiêu (Points Refund):** Toàn bộ số điểm thưởng khách hàng đã trích ra để giảm giá cho đơn hàng đó được hoàn lại nguyên vẹn 100% vào ví điểm của khách.
   - **Mở khóa Mã giảm giá (Voucher Reactivation):** Mã Voucher đã áp dụng cho đơn hàng bị hủy sẽ được mở khóa để khách hàng có thể sử dụng lại cho lần đặt hàng sau, với điều kiện mã Voucher đó vẫn còn nằm trong thời hạn hiệu lực của chương trình.
   - **Hủy bỏ Điểm tích lũy dự kiến (Cancel Pending Points):** Toàn bộ số điểm thưởng dự kiến nhận được từ đơn hàng bị hủy sẽ bị hủy bỏ hoàn toàn khỏi hệ thống (triệt tiêu lỗ hổng cày điểm ảo).

---

## 1.5. Quy định Đổi trả Hàng hóa & Chính sách Hậu mãi (Return & Exchange Policy)

Do đặc thù mỹ phẩm là sản phẩm tiếp xúc trực tiếp lên da và ảnh hưởng đến sức khỏe người tiêu dùng, quy định đổi trả được áp dụng nghiêm ngặt theo tiêu chuẩn an toàn y tế.

### Điều 10. Thời hạn và Điều kiện Chấp nhận Đổi trả
1. **Thời hạn tiếp nhận:** Cửa hàng tiếp nhận yêu cầu đổi trả trong vòng **07 ngày** kể từ thời điểm đơn hàng chuyển sang trạng thái "Hoàn tất" (khách đã ký nhận kiện hàng).
2. **Điều kiện đổi trả hợp lệ:**
   - Sản phẩm giao sai chủng loại, sai mã màu son, sai dung tích so với thông tin đơn hàng đã đặt.
   - Sản phẩm bị vỡ nắp, rách tem niêm phong hoặc rò rỉ dung dịch do quá trình vận chuyển.
   - Sản phẩm có lỗi từ nhà sản xuất (vón cục, biến đổi mùi, đổi màu bất thường dù còn hạn dùng).
   - Khách hàng cung cấp được **Video quay lại toàn bộ quá trình mở hộp hàng (Video Unboxing)** rõ mã vận đơn và tình trạng hộp còn nguyên vẹn trước khi rạch băng keo.
3. **Các trường hợp từ chối đổi trả:**
   - Sản phẩm đã bị bóc seal, xé tem niêm phong và đã qua sử dụng (trừ trường hợp phát hiện lỗi chất lượng do nhà sản xuất).
   - Quá thời hạn 07 ngày kể từ khi nhận hàng.
   - Hư hỏng do khách hàng bảo quản sai hướng dẫn (để nơi nhiệt độ cao, tiếp xúc ánh nắng trực tiếp làm chảy son/hỏng kem).
   - Khách hàng không có video mở hộp đối chiếu chứng minh hàng bị hư vỡ từ trước.

### Điều 11. Phân bổ Chi phí Vận chuyển Đổi trả
1. **Lỗi do cửa hàng hoặc nhà sản xuất:** Cửa hàng PinkyCloud chịu **100% chi phí vận chuyển 2 chiều** để thu hồi hàng cũ và gửi sản phẩm mới nguyên vẹn đến tận tay khách hàng.
2. **Khách hàng có nhu cầu đổi ý (Đổi sang màu son/dung tích khác):** Chỉ chấp nhận khi sản phẩm cũ **còn nguyên vẹn tem seal 100%** và chưa bóc hộp. Khách hàng chịu **100% chi phí vận chuyển 2 chiều** và thanh toán phần tiền chênh lệch (nếu sản phẩm mới có giá cao hơn; nếu sản phẩm mới giá thấp hơn, cửa hàng không hoàn lại tiền thừa).

---

# PHẦN 2: 🎯 BỘ CÂU HỎI "XOÁY" BẢO VỆ ĐỒ ÁN (DEFENSE Q&A)

Dưới đây là 5 câu hỏi tình huống thực chiến hóc búa nhất mà Hội đồng Bảo vệ Đồ án thường sử dụng để phản biện và bắt bẻ logic nghiệp vụ của sinh viên, đi kèm phương án giải trình chuẩn mực theo các điều khoản nghiệp vụ tại Phần 1.

---

### Câu hỏi 1: Xử lý Tranh chấp Tồn kho & Đặt hàng Đồng thời (Race Condition)
* **Câu hỏi từ Giáo viên:**  
  *"Giả sử trong kho chỉ còn đúng 01 chai Serum trị mụn cuối cùng. Tại cùng một thời điểm, có 2 khách hàng A và B cùng thêm sản phẩm này vào giỏ hàng và cùng nhấn nút Đặt hàng gần như cùng một giây. Tại sao hệ thống của bạn không trừ kho ngay khi khách thêm vào giỏ? Và nếu cả 2 người cùng gửi yêu cầu đặt hàng đồng thời, hệ thống giải quyết như thế nào để không bị bán âm tồn kho?"*

* **Cách Sinh viên Đáp trả tự tin:**  
  > "Dạ thưa Thầy/Cô, hệ thống của nhóm em được thiết kế tuân thủ nghiêm ngặt theo **Điều 2 và Điều 3 (Bộ quy định Quản lý Giỏ hàng & Tồn kho)**:
  > 1. **Về lý do không trừ kho khi thêm vào giỏ hàng:** Nếu hệ thống trừ kho ngay khi thêm vào giỏ, bất kỳ người dùng nào (hoặc đối thủ cạnh tranh) chỉ cần mở nhiều tài khoản và cho hết hàng vào giỏ là có thể làm 'đóng băng' toàn bộ kho hàng của shop mà không cần trả tiền, khiến khách hàng thật không thể mua được. Vì vậy, giỏ hàng chỉ mang tính chất ghi nhớ tạm thời.
  > 2. **Về cách xử lý tranh chấp khi 2 khách cùng bấm mua 1 món cuối cùng:** Khi 2 yêu cầu gửi về máy chủ, giao dịch nào hoàn tất bước ghi nhận đơn hàng trước (First-Come, First-Served) sẽ khóa và trừ ngay 1 tồn kho về 0. Khi giao dịch của khách hàng thứ 2 được xử lý tiếp theo, hệ thống kiểm tra tồn kho thấy số lượng $= 0$ nên sẽ từ chối tạo đơn, đồng thời thông báo rõ ràng cho khách hàng thứ 2 là 'Sản phẩm vừa hết hàng'. Nhờ đó, hệ thống hoàn toàn loại bỏ rủi ro bán âm kho và bảo đảm tính công bằng tuyệt đối."

---

### Câu hỏi 2: Chống Trục lợi Hủy đơn & Lỗ hổng Cày Điểm thưởng / Voucher
* **Câu hỏi từ Giáo viên:**  
  *"Khách hàng đặt một đơn hàng mỹ phẩm trị giá 1.000.000 VNĐ, sử dụng một mã Voucher giảm 200.000 VNĐ và tiêu 50 Điểm thưởng (trị giá 50.000 VNĐ). Đơn hàng sau đó được tích lũy thêm điểm thưởng mới. Nếu ngay sau đó khách hàng bấm Hủy đơn hàng, hệ thống xử lý hoàn trả điểm, voucher và điểm thưởng mới như thế nào để khách không thể lợi dụng tạo đơn rồi hủy để cày điểm vô hạn?"*

* **Cách Sinh viên Đáp trả tự tin:**  
  > "Dạ thưa Thầy/Cô, kịch bản này đã được nhóm dự phòng và xử lý triệt để theo **Điều 6, Điều 8 và Điều 9 (Bộ quy định Điểm thưởng và Hủy đơn hàng)**:
  > 1. **Về điểm thưởng tích lũy mới:** Theo Điều 6, điểm thưởng chỉ được duyệt và cộng chính thức sau khi đơn hàng chuyển sang trạng thái **'Hoàn tất' (Giao hàng và thu tiền thành công)**. Ở bước khách vừa đặt, điểm ở trạng thái chờ duyệt. Khi khách bấm hủy đơn, số điểm chờ duyệt này lập tức bị hủy bỏ 100%, khách không nhận được bất kỳ điểm thưởng nào từ đơn hủy.
  > 2. **Về 50 điểm thưởng cũ khách đã tiêu:** Hệ thống sẽ hoàn trả đúng 50 điểm này về lại ví điểm của khách hàng theo Điều 9.
  > 3. **Về mã Voucher 200.000 VNĐ:** Mã Voucher sẽ được mở khóa lại với điều kiện thời hạn chương trình khuyến mãi vẫn còn hiệu lực.
  > 4. **Về tồn kho:** Hàng hóa được cộng lại vào kho ngay lập tức.
  > Do đó, toàn bộ tài nguyên trở về đúng trạng thái ban đầu, người dùng không thể thực hiện bất kỳ hành vi cày điểm hay chiếm đoạt ưu đãi nào."

---

### Câu hỏi 3: Kiểm soát Xung đột Cộng dồn Khuyến mãi (Promotion Stacking Abuse)
* **Câu hỏi từ Giáo viên:**  
  *"Một sản phẩm có giá gốc 500.000 VNĐ đang trong đợt giảm giá trực tiếp 20% còn 400.000 VNĐ. Khách hàng nhập thêm một Voucher giảm 10% toàn sàn, và muốn dùng thêm 300 điểm thưởng (tương đương 300.000 VNĐ). Làm sao hệ thống đảm bảo doanh nghiệp không bị bán lỗ dưới giá vốn và thứ tự tính tiền diễn ra như thế nào?"*

* **Cách Sinh viên Đáp trả tự tin:**  
  > "Dạ thưa Thầy/Cô, hệ thống xử lý bài toán này thông qua **Quy tắc Áp dụng Chiết khấu Đa tầng (Điều 4) và Hạn mức Trần tiêu điểm (Điều 7)**:
  > - **Tầng 1 (Giảm trực tiếp):** Giá sản phẩm giảm 20% còn **400.000 VNĐ** (Giá trị A).
  > - **Tầng 2 (Áp dụng Voucher 10%):** Voucher 10% được tính trên giá trị sau giảm trực tiếp (400.000 VNĐ), tức giảm tiếp **40.000 VNĐ**. Tiền hàng sau voucher còn **360.000 VNĐ** (Giá trị B).
  > - **Tầng 3 (Áp dụng Tiêu điểm thưởng):** Khách có 300 điểm (300.000 VNĐ), tuy nhiên theo **Điều 7, mức tiêu điểm tối đa không được vượt quá 50% giá trị tiền hàng sau voucher**. Do đó, mức giảm tối đa từ điểm chỉ là: $360.000 \times 50\% = 180.000\text{ VNĐ}$ (tương đương tiêu 180 điểm). Hệ thống chỉ trừ 180 điểm của khách, 120 điểm còn lại vẫn giữ nguyên trong tài khoản.
  > - **Tầng 4 (Số tiền thực trả):** Tiền hàng thực trả là $360.000 - 180.000 = \mathbf{180.000\text{ VNĐ}}$ (+ cước vận chuyển nếu có).
  > Nhờ quy tắc khống chế trần 50% và phân tầng chiết khấu này, doanh nghiệp luôn bảo toàn được dòng tiền thu về tối thiểu, triệt tiêu hoàn toàn rủi ro bán âm tiền hoặc thủng biên lợi nhuận."

---

### Câu hỏi 4: Điều chỉnh Đơn hàng khi đang Chuẩn bị & Rủi ro Lệch Tồn kho Thực xuất
* **Câu hỏi từ Giáo viên:**  
  *"Khi khách hàng đã đặt đơn gồm 03 thỏi son, nhưng sau đó gọi điện cho nhân viên xin giảm xuống 01 thỏi son vì đổi ý. Lúc này quản trị viên vào trang quản trị để sửa số lượng từ 3 thành 1. Hệ thống xử lý cập nhật đơn hàng, hoàn trả tồn kho của 2 thỏi son còn lại và tính toán lại tiền đơn hàng như thế nào?"*

* **Cách Sinh viên Đáp trả tự tin:**  
  > "Dạ thưa Thầy/Cô, tình huống này được định nghĩa chuẩn xác trong **Nghiệp vụ Quản lý Chi tiết Đơn hàng (Quy định 1.4 và Mục b.4 của Tài liệu Yêu cầu)**:
  > 1. Khi quản trị viên nhập số lượng mới là 1 và nhấn 'Cập nhật', hệ thống sẽ kiểm tra độ chênh lệch số lượng: $\Delta = 3 - 1 = 2$ sản phẩm.
  > 2. Hệ thống lập tức thực hiện cộng hoàn trả 2 đơn vị sản phẩm này trở lại vào tồn kho thực tế của mã son đó để phục vụ khách hàng khác.
  > 3. Hệ thống tính lại thành tiền của dòng sản phẩm theo giá bán tại thời điểm đặt đơn ($1 \times \text{Đơn giá lịch sử}$), sau đó cập nhật lại Tổng tiền thanh toán của toàn bộ đơn hàng.
  > 4. Nếu đơn hàng này ban đầu $> 500.000$ VNĐ được miễn phí vận chuyển, nhưng sau khi giảm số lượng khiến tổng tiền $< 500.000$ VNĐ, hệ thống sẽ tự động cộng thêm phí vận chuyển tiêu chuẩn theo đúng chính sách khu vực.
  > Toàn bộ quy trình này đảm bảo phiếu xuất kho đưa cho bộ phận đóng gói và số tiền thu hộ COD của bưu tá luôn khớp đúng 100% với thực tế hàng xuất."

---

### Câu hỏi 5: Xử lý Khiếu nại Hàng Hư hỏng trong Quá trình Vận chuyển & Quy trình Đổi trả
* **Câu hỏi từ Giáo viên:**  
  *"Mỹ phẩm là mặt hàng dễ vỡ (như chai serum thủy tinh, phấn nén). Nếu khách hàng nhận hàng và phản ánh chai serum bị vỡ nát, nhưng cửa hàng cho rằng lỗi do khách làm rơi sau khi nhận. Hệ thống và quy định của cửa hàng căn cứ vào đâu để phân định trách nhiệm và xử lý đổi trả minh bạch?"*

* **Cách Sinh viên Đáp trả tự tin:**  
  > "Dạ thưa Thầy/Cô, quy trình giải quyết khiếu nại và đổi trả của cửa hàng dựa trên **Điều 10 và Điều 11 (Chính sách Đổi trả & Hậu mãi)**:
  > 1. **Yêu cầu bắt buộc về Bằng chứng:** Cửa hàng quy định rõ ràng trên website và trên tem dán ngoài kiện hàng: Khách hàng khi nhận kiện hàng bắt buộc phải quay **Video mở hộp (Video Unboxing)** liền mạch, không cắt ghép, thể hiện rõ mã vận đơn và tình trạng hộp trước khi bóc seal.
  > 2. **Xác định trách nhiệm:**
  >    - Nếu video thể hiện rõ chai serum đã bị vỡ và dung dịch loang ra từ trong hộp trước khi bóc: Cửa hàng xác định lỗi thuộc về khâu đóng gói hoặc đơn vị vận chuyển. Cửa hàng sẽ chịu 100% phí vận chuyển 2 chiều để gửi ngay sản phẩm mới bù cho khách trong vòng 24 giờ, đồng thời làm việc với đơn vị vận chuyển để yêu cầu bảo hiểm bồi thường.
  >    - Nếu khách không có video mở hộp hoặc video thể hiện tem seal đã bị rạch từ trước, cửa hàng có quyền từ chối tiếp nhận đổi trả theo đúng điều khoản đã niêm yết để phòng tránh gian lận tráo đổi hàng giả/hàng vỡ.
  > Quy định minh bạch này vừa bảo vệ quyền lợi tối đa cho khách hàng chân chính, vừa bảo vệ cửa hàng khỏi các hành vi trục lợi ác ý."
