# THU THẬP, LÀM RÕ YÊU CẦU CỦA ỨNG DỤNG

**Nhóm 12 – Thành viên nhóm:**
- Phạm Thanh Huy (Nhóm Trưởng)
- Trần Gia Hào
- Trình Hoàng Kỳ
- Nguyễn Công Huy

**Tên ứng dụng:** CHƯƠNG TRÌNH HỆ THỐNG QUẢN LÝ VÀ BÁN MỸ PHẨM TRỰC TUYẾN PINKYCLOUD  
**Thời gian thực hiện:** Học kỳ 1 – Năm học 2025-2026 (15 tuần)

---

## MỤC LỤC

1. [Thu thập yêu cầu](#1-thu-thập-yêu-cầu)  
   a. [Khảo sát hiện trạng](#a-khảo-sát-hiện-trạng)  
   - Quản lý bán hàng & Giỏ hàng trực tuyến  
   - Quản lý sản phẩm mỹ phẩm  
   - Quản lý danh mục hàng hóa  
   - Quản lý đơn hàng & Chi tiết đơn hàng  
   - Quản lý khách hàng  
   - Quản lý hóa đơn & Thống kê doanh thu  
   
   b. [Quy trình nghiệp vụ](#b-quy-trình-nghiệp-vụ)  
   - b.1. Quản lý bán hàng & mua sắm trực tuyến  
   - b.2. Quản lý danh mục sản phẩm (dành cho quản lý)  
   - b.3. Quản lý sản phẩm & tồn kho (dành cho quản lý)  
   - b.4. Quản lý đơn hàng & cập nhật chi tiết đơn (dành cho quản lý)  
   - b.5. Quản lý khách hàng  
   - b.6. Quản lý hóa đơn & Thống kê doanh thu  

   c. [Quy định](#c-quy-định)  
   - c.1. Quy định về Bán hàng, Giỏ hàng & Thanh toán  
   - c.2. Quy định về Quản lý Sản phẩm & Danh mục  
   - c.3. Quy định về Quản lý Đơn hàng & Cập nhật số lượng  
   - c.4. Quy định về Quản lý Khách hàng & Giao nhận  
   - c.5. Quy định về Phân quyền & Nhân viên quản trị  
   - c.6. Quy định về Quản lý Thống kê & Hóa đơn  

2. [Danh sách các câu hỏi khi thu thập và làm rõ yêu cầu của ứng dụng](#2-danh-sách-các-câu-hỏi-khi-thu-thập-và-làm-rõ-yêu-cầu-của-ứng-dụng)  
3. [Yêu cầu chức năng / phi chức năng của ứng dụng](#3-yêu-cầu-chức-năng--phi-chức-năng-của-ứng-dụng)  
4. [Sơ đồ phân cấp chức năng của ứng dụng](#4-sơ-đồ-phân-cấp-chức-năng-của-ứng-dụng)  
5. [Các chức năng chính cho ứng dụng (Mục tiêu của ứng dụng)](#5-các-chức-năng-chính-cho-ứng-dụng-mục-tiêu-của-ứng-dụng)  

---

## 1. Thu thập yêu cầu

### a. Khảo sát hiện trạng

Ngành kinh doanh mỹ phẩm và chăm sóc sắc đẹp tại Việt Nam hiện đang có sự phát triển rất mạnh mẽ, đặc biệt là phân khúc phục vụ đối tượng khách hàng trẻ như học sinh, sinh viên và nhân viên văn phòng. Cửa hàng mỹ phẩm **PinkyCloud** là một đơn vị kinh doanh bán lẻ chuyên phân phối các dòng sản phẩm chăm sóc da, trang điểm và chăm sóc cơ thể chính hãng tại TP. Hồ Chí Minh. Cơ cấu tổ chức cơ bản của cửa hàng bao gồm: bộ phận tư vấn & bán hàng (chịu trách nhiệm tư vấn công dụng, tiếp nhận nhu cầu và chốt đơn), bộ phận quản lý kho & sản phẩm (kiểm kê hàng hóa, nhập kho, phân loại danh mục sản phẩm), bộ phận đóng gói & giao nhận (kiểm tra đơn, đóng gói, bàn giao cho bưu tá vận chuyển), và bộ phận quản lý - kế toán (theo dõi dòng tiền, thống kê doanh thu và giám sát nhân viên).

Hiện tại, hoạt động bán hàng của cửa hàng đang diễn ra đồng thời tại quầy bán lẻ trực tiếp và trên các kênh trực tuyến qua mạng xã hội (Facebook Fanpage, Instagram, Zalo cá nhân). Tuy nhiên, phương thức quản lý và quy trình vận hành giữa các bộ phận này vẫn còn phụ thuộc rất lớn vào ghi chép sổ sách giấy tờ thủ công và các bảng tính Excel rời rạc.

Năm nghiệp vụ chính được xác định tại cửa hàng mỹ phẩm PinkyCloud bao gồm: quản lý bán hàng & giỏ hàng, quản lý sản phẩm, quản lý danh mục hàng hóa, quản lý đơn hàng, và quản lý khách hàng - thống kê doanh thu.

---

#### Quản lý bán hàng & Giỏ hàng trực tuyến:
1) **Bán hàng & Tiếp nhận nhu cầu khách hàng:** Hiện nay, quy trình bán hàng trực tuyến được thực hiện qua hình thức nhắn tin tư vấn trực tiếp giữa nhân viên trực ca và khách hàng. Khi khách hàng có nhu cầu tìm hiểu hoặc mua mỹ phẩm, họ sẽ gửi tin nhắn đến Fanpage hoặc Zalo để hỏi về hình ảnh sản phẩm, tình trạng còn hàng, bảng màu son hoặc dung tích phù hợp. Nhân viên tiến hành mở album ảnh trên máy điện thoại để gửi tư vấn, sau đó phải mở file Excel kiểm tra số lượng tồn trên máy tính rồi mới báo giá cho khách.
- *Hạn chế về thời gian phản hồi:* Do phải trả lời từng tin nhắn thủ công và tra cứu qua nhiều bước, thời gian chờ phản hồi của khách hàng thường kéo dài từ 15 đến 30 phút. Khách hàng mua mỹ phẩm có tâm lý muốn xem hàng và mua ngay; sự chậm trễ này khiến hơn 30% khách hàng từ bỏ việc mua sắm hoặc chuyển sang mua tại các cửa hàng khác có hệ thống bán hàng tự phục vụ trực tiếp.
- *Đặc thù trải nghiệm người dùng ngành mỹ phẩm:* Khách hàng chủ yếu của cửa hàng là giới trẻ, yêu thích sự trẻ trung, cá tính và tính trực quan. Mỹ phẩm là ngành hàng có rất nhiều biến thể tương tự nhau (ví dụ: cùng một dòng son nhưng có 5-10 tone màu khác nhau; cùng một loại kem dưỡng nhưng có bản 30ml và 50ml). Nếu thiết kế giao diện nhạt nhòa, các khối thông tin không rõ ràng sẽ khiến khách hàng dễ nhìn nhầm mã màu hoặc chọn sai phân loại. Do đó, doanh nghiệp đặt ra yêu cầu giao diện phải có phong cách thiết kế dứt khoát, các khối thẻ sản phẩm có đường viền đậm rõ nét, độ tương phản cao, nút bấm to bản để người mua dễ nhận biết và thao tác chính xác trên điện thoại di động mà không bị bấm nhầm.
- *Đặc thù môi trường truy cập của khách hàng:* Đại đa số khách hàng lướt xem và mua mỹ phẩm trên điện thoại khi đang di chuyển, sử dụng mạng di động (3G/4G) đôi khi không ổn định. Nếu hệ thống yêu cầu nạp tải quá nhiều dữ liệu phức tạp trên máy người dùng, trang web sẽ bị trắng hoặc giật lag làm khách nản lòng. Do đó, hệ thống mới phải đảm bảo trang web được nạp và hiển thị đầy đủ thông tin sản phẩm tức thì (dưới 1.5 giây), nội dung văn bản và hình ảnh hiển thị trọn vẹn ngay khi vừa mở trang, đồng thời thông tin sản phẩm dễ dàng tiếp cận tới các công cụ tìm kiếm của người dùng.

2) **Giỏ hàng tạm thời & Kiểm soát số lượng mua:** Trong quá trình chọn đồ, khách hàng thường xuyên thay đổi ý định: muốn chọn thêm mặt nạ dưỡng da, bớt đi một thỏi son, hoặc tăng số lượng kem chống nắng để mua chung cùng bạn bè. Hiện tại, nhân viên ghi chép các món khách chọn vào một cuốn sổ tay nháp; mỗi lần khách đổi ý, nhân viên lại gạch xóa rồi tính toán lại tiền bằng máy tính cầm tay. Việc này rất dễ gây ra sai sót, tính nhầm tổng tiền hoặc soạn thiếu hàng khi chuyển sang khâu đóng gói.
- Hệ thống mới sẽ cung cấp tính năng Giỏ hàng trực tuyến đóng vai trò như một chiếc túi mua sắm cá nhân. Khách hàng có thể tự do thêm sản phẩm vào giỏ, xem danh sách các món đồ đã chọn, điều chỉnh tăng giảm số lượng hoặc loại bỏ những món không còn nhu cầu. Mỗi khi có sự thay đổi, hệ thống sẽ tự động tính toán lại tổng tiền tạm tính ngay trên màn hình để khách hàng dễ dàng theo dõi ngân sách chi tiêu.
- Để hạn chế tình trạng gom hàng đầu cơ số lượng lớn trong các đợt mở bán những bộ sưu tập son hay mỹ phẩm giới hạn (gây thiếu hụt hàng cho những khách hàng khác), cửa hàng áp dụng quy định mỗi lần mua một mã sản phẩm trong giỏ không được vượt quá 10 đơn vị.

3) **Thanh toán & Xác nhận đơn hàng:** Sau khi khách hàng hoàn tất việc lựa chọn sản phẩm trong giỏ hàng và quyết định mua, họ sẽ chuyển sang bước xác nhận đơn. Khách hàng cung cấp đầy đủ thông tin nhận hàng gồm: Họ và tên người nhận, Số điện thoại liên hệ, Địa chỉ giao hàng chi tiết (Số nhà, Tên đường, Phường/Xã, Quận/Huyện, Tỉnh/Thành phố) và Ghi chú giao hàng nếu có yêu cầu đặc biệt về thời gian nhận. Khách hàng lựa chọn một trong hai hình thức thanh toán được cửa hàng hỗ trợ: Thanh toán tiền mặt khi nhận hàng (COD) hoặc Chuyển khoản ngân hàng.

- **Công thức tính tổng tiền thanh toán:**
$$\text{Tổng tiền thanh toán} = \sum (\text{Đơn giá sản phẩm} \times \text{Số lượng}) + \text{Phí vận chuyển} - \text{Chiết khấu ưu đãi (nếu có)}$$

*Bảng quy định mức phí vận chuyển áp dụng theo khu vực địa lý:*

| Khu vực nhận hàng | Phạm vi địa bàn áp dụng | Cước phí vận chuyển tiêu chuẩn | Thời gian giao hàng dự kiến |
| :--- | :--- | :--- | :--- |
| **Nội thành TP.HCM** | Các quận trung tâm (Quận 1, 3, 5, 10, Bình Thạnh, Phú Nhuận...) | 20.000 VNĐ | Trong ngày hoặc 24 giờ |
| **Ngoại thành TP.HCM** | Các quận/huyện vùng ven (Quận 12, Hóc Môn, Bình Chánh, Củ Chi...) | 30.000 VNĐ | 1 - 2 ngày làm việc |
| **Các tỉnh miền Nam** | Các tỉnh thuộc Đông Nam Bộ và Đồng bằng Sông Cửu Long | 35.000 VNĐ | 2 - 3 ngày làm việc |
| **Miền Trung & Miền Bắc** | Các tỉnh thành khu vực miền Trung, Tây Nguyên và phía Bắc | 40.000 VNĐ | 3 - 5 ngày làm việc |
| **Chính sách Đơn hàng lớn** | Áp dụng cho mọi đơn hàng có tổng tiền sản phẩm $\ge 500.000$ VNĐ | **Miễn phí vận chuyển (0 VNĐ)** | Theo thời gian từng khu vực |

*Ví dụ thực tế:* Một khách hàng tại Quận 5, TP.HCM đặt mua 01 chai Sữa rửa mặt Tràm Trà giá 180.000 VNĐ và 02 thỏi Son kem lì giá 220.000 VNĐ/thỏi. Tổng tiền hàng được tính là $180.000 + (220.000 \times 2) = 620.000$ VNĐ. Vì tổng tiền hàng lớn hơn hạn mức 500.000 VNĐ nên đơn hàng được áp dụng chính sách miễn cước phí vận chuyển 20.000 VNĐ. Số tiền thanh toán cuối cùng khách hàng phải trả khi nhận hàng (COD) là đúng 620.000 VNĐ.

---

#### Quản lý sản phẩm mỹ phẩm:
Dữ liệu về các mặt hàng mỹ phẩm là cơ sở quan trọng nhất để toàn bộ hoạt động bán lẻ diễn ra suôn sẻ. Mỗi sản phẩm bao gồm các thông tin quản lý nghiệp vụ như: Mã sản phẩm, Tên sản phẩm, Thương hiệu, Phân loại danh mục, Đơn giá niêm yết, Số lượng tồn kho thực tế, Mô tả công dụng, Thành phần chi tiết, Hướng dẫn sử dụng và Hình ảnh minh họa sản phẩm.

Hiện nay, việc theo dõi hàng hóa được thực hiện bằng một file Excel chung do nhân viên kho và chủ cửa hàng cùng cập nhật. Mỗi khi nhập thêm hàng mới (ví dụ: nhập thêm 50 lọ Tinh chất dưỡng sáng da), nhân viên kho phải mở máy tính, tìm dòng sản phẩm đó rồi gõ tay cộng dồn số lượng. Quy trình thủ công này thường xuyên dẫn đến tình trạng gõ nhầm (nhập thừa hoặc thiếu chữ số 0, hoặc nhập nhầm vào dòng sản phẩm khác có tên tương tự), khiến số lượng hàng hiển thị trên sổ sách lệch hoàn toàn so với số lượng thực tế nằm trên kệ trưng bày.

Hậu quả là khi khách hàng đặt mua một sản phẩm đang có số tồn "ảo" trên sổ sách, nhân viên chốt đơn và in phiếu xong xuôi, đến khi người đóng gói đi tìm hàng mới phát hiện đã hết sạch hàng. Doanh nghiệp buộc phải gọi điện xin lỗi khách hàng, đề nghị đổi món khác hoặc hủy đơn hàng, làm mất uy tín nghiêm trọng trong mắt khách hàng. Phân hệ Quản lý Sản phẩm mới sẽ chuẩn hóa toàn bộ hồ sơ mặt hàng, hỗ trợ thêm mới, chỉnh sửa đơn giá, điều chỉnh tồn kho sau mỗi kỳ kiểm kê, tra cứu nhanh theo tên sản phẩm hoặc lọc theo nhóm danh mục, đồng thời ngăn chặn các sai sót nhập liệu (như nhập đơn giá hoặc số lượng âm).

---

#### Quản lý danh mục hàng hóa:
Mỹ phẩm là ngành hàng đa dạng về chủng loại và liên tục phát triển thêm nhiều dòng sản phẩm mới theo xu hướng thị trường (Nhóm Chăm sóc da: Sữa rửa mặt, Nước cân bằng, Tinh chất/Serum, Kem dưỡng ẩm, Kem chống nắng; Nhóm Trang điểm: Son môi, Phấn nền/Cushion, Phấn mắt, Mascara; Nhóm Chăm sóc cơ thể: Sữa tắm, Dưỡng thể, Tẩy tế bào chết).

Trước đây, cửa hàng chưa có quy chuẩn phân loại danh mục khoa học; nhân viên tự tạo các album ảnh trên mạng xã hội một cách tự phát, dẫn đến việc khách hàng mới rất khó định hình cửa hàng đang có những nhóm hàng nào để tìm kiếm. Hệ thống mới sẽ thiết lập phân hệ Quản lý Danh mục hàng hóa bài bản, cho phép quản lý xem danh sách các nhóm hàng, tạo danh mục mới, cập nhật tên và mô tả, đồng thời kiểm soát chặt chẽ mối liên hệ giữa danh mục với các sản phẩm bên trong. Để đảm bảo không làm mất dữ liệu sản phẩm, hệ thống áp dụng quy định không cho phép xóa danh mục nếu danh mục đó vẫn đang có các mặt hàng trực thuộc đang kinh doanh.

---

#### Quản lý đơn hàng & Chi tiết đơn hàng:
Đơn hàng là chứng từ xác nhận giao dịch thương mại giữa khách hàng và cửa hàng PinkyCloud. Mỗi đơn hàng lưu trữ đầy đủ các thông tin: Mã đơn hàng duy nhất, Thời gian tạo đơn, Thông tin người nhận hàng (Họ tên, SĐT, Địa chỉ, Ghi chú), Hình thức thanh toán đã chọn, Danh sách chi tiết các mặt hàng đặt mua (Tên sản phẩm, Đơn giá tại thời điểm mua, Số lượng đặt, Thành tiền từng dòng) và Tổng giá trị tiền cần thu.

Trong thực tế vận hành, có rất nhiều trường hợp sau khi đặt hàng trực tuyến xong, khách hàng liên hệ lại qua điện thoại để xin thay đổi số lượng (ví dụ: muốn giảm từ 3 thỏi son xuống 2 thỏi vì lý do tài chính, hoặc tăng thêm 1 chai nước hoa hồng để dùng cùng người thân). Trước đây, nhân viên phải lấy bút gạch xóa trên phiếu in giấy, rất dễ quên khi bốc hàng đóng gói và làm sai lệch số tiền thu hộ của bên giao vận (COD).

Hệ thống mới cung cấp tính năng Quản lý Chi tiết Đơn hàng: Quản trị viên có thể tra cứu đơn hàng theo mã đơn hoặc số điện thoại, mở xem chi tiết từng món hàng và trực tiếp điều chỉnh số lượng thực tế của từng sản phẩm trong đơn theo đúng thỏa thuận với khách. Hệ thống sẽ tự động tính lại thành tiền của từng dòng và cập nhật lại Tổng tiền thanh toán của toàn bộ đơn hàng một cách chính xác, đảm bảo phiếu xuất kho và số tiền thu của khách luôn khớp nhau tuyệt đối.

---

#### Quản lý khách hàng:
Khách hàng của PinkyCloud bao gồm cả khách mua trực tiếp tại cửa hàng và khách đặt mua trực tuyến. Trước đây, thông tin khách hàng chỉ được lưu rời rạc trên các tờ phiếu dán kiện hàng hoặc trôi dần trong lịch sử tin nhắn mạng xã hội. Cửa hàng không có hệ thống lưu trữ tập trung nên không thể tra cứu được lịch sử mua sắm của khách quen, không biết khách có làn da gì hay chu kỳ tái mua sản phẩm ra sao để phục vụ chăm sóc và tư vấn tiếp theo.

Hệ thống mới sẽ tự động lưu trữ và chuẩn hóa hồ sơ khách hàng sau mỗi lần giao dịch thành công (Họ tên, Số điện thoại, Địa chỉ giao hàng quen thuộc và danh sách các đơn hàng đã đặt trong quá khứ), giúp nhân viên dễ dàng tra cứu khi tiếp nhận phản hồi hoặc xử lý yêu cầu hỗ trợ sau bán hàng.

---

#### Quản lý hóa đơn & Thống kê doanh thu:
Việc theo dõi và tổng hợp doanh thu bán hàng cuối ngày hiện do chủ cửa hàng thực hiện bằng cách cộng thủ công từng hóa đơn giấy bán lẻ và sao kê chuyển khoản ngân hàng. Vào những ngày khuyến mãi lớn (như ngày lễ 8/3, 20/10, dịp siêu sale cuối năm), số lượng đơn hàng lên đến hàng trăm đơn khiến việc tổng kết sổ sách kéo dài nhiều giờ đồng hồ sau khi đóng cửa, rất dễ phát sinh sai sót nhầm lẫn tiền bạc mà không thể đối soát lại nguyên nhân thất thoát.

Hệ thống mới tự động tổng hợp toàn bộ các đơn hàng đã phát sinh, cung cấp báo cáo thống kê trực quan theo ngày, theo tháng, hiển thị tổng doanh thu, số lượng đơn hàng đã xử lý và số lượng sản phẩm bán chạy nhất, giúp chủ cửa hàng kịp thời nắm bắt tình hình kinh doanh và chủ động trong việc lên kế hoạch nhập hàng cho các kỳ tiếp theo.

---

### b. Quy trình nghiệp vụ

#### b.1. Quản lý bán hàng & mua sắm trực tuyến:

##### Mua hàng & Đặt hàng trực tuyến:
- **Bước 1: Tiếp cận và Khám phá sản phẩm:**
  - Khách hàng truy cập vào Trang chủ PinkyCloud để xem các dòng mỹ phẩm mới nhất, sản phẩm bán chạy và các chương trình nổi bật.
  - Khách hàng có thể lọc sản phẩm theo từng Danh mục chuyên biệt (Chăm sóc da, Trang điểm, Chăm sóc cơ thể...) hoặc nhập từ khóa tìm kiếm.
- **Bước 2: Xem chi tiết sản phẩm:**
  - Khách hàng nhấn vào thẻ sản phẩm để xem ảnh chi tiết, tên hàng, thương hiệu, đơn giá niêm yết, tình trạng tồn kho và công dụng/thành phần.
- **Bước 3: Thêm vào Túi mua sắm (Giỏ hàng):**
  - Khách hàng chọn số lượng mong muốn (mặc định là 1, tối đa 10 sản phẩm cho một mã hàng).
  - Khách hàng nhấn nút "Thêm vào giỏ hàng". Hệ thống cập nhật giỏ hàng tạm thời và hiển thị thông báo thành công.
- **Bước 4: Kiểm tra và Quản lý Giỏ hàng:**
  - Khách hàng chuyển sang trang Giỏ hàng để xem danh sách toàn bộ các món đồ đã chọn.
  - Khách hàng có thể tăng/giảm số lượng từng món hoặc xóa bỏ món hàng không muốn mua nữa. Hệ thống tự động tính lại tổng tiền tạm tính.
- **Bước 5: Xác nhận thông tin Đặt hàng (Checkout):**
  - Khách hàng nhấn nút "Tiến hành Đặt hàng".
  - Khách hàng nhập thông tin giao nhận bắt buộc: Họ và tên người nhận, Số điện thoại liên hệ, Địa chỉ giao hàng chi tiết (Số nhà, Tên đường, Phường/Xã, Quận/Huyện, Tỉnh/Thành phố) và Ghi chú giao hàng (nếu có).
  - Khách hàng lựa chọn phương thức thanh toán: Thanh toán khi nhận hàng (COD) hoặc Chuyển khoản ngân hàng.
- **Bước 6: Tạo đơn và Hoàn tất giao dịch:**
  - Khách hàng nhấn "Xác nhận Đặt hàng".
  - Hệ thống kiểm tra tính hợp lệ dữ liệu, tự động tạo Mã đơn hàng mới, lưu thông tin giao dịch vào hệ thống, làm rỗng giỏ hàng tạm thời và hiển thị màn hình thông báo Đặt hàng thành công kèm chi tiết mã đơn.

---

#### b.2. Quản lý danh mục sản phẩm (dành cho quản lý):

##### 1. Xem danh sách danh mục:
- **Bước 1:** Quản lý truy cập vào mục Quản lý Danh mục trên thanh điều hướng quản trị.
- **Bước 2:** Hệ thống hiển thị bảng danh sách các danh mục hiện có gồm: Mã danh mục, Tên danh mục, Mô tả ngắn và các thao tác tương ứng (Sửa, Xóa).

##### 2. Thêm mới danh mục:
- **Bước 1:** Quản lý nhấn chọn nút "Thêm danh mục mới".
- **Bước 2:** Nhập các thông tin cần thiết: Tên danh mục (bắt buộc, không được để trống), Mô tả chi tiết.
- **Bước 3:** Quản lý nhấn "Lưu danh mục". Hệ thống kiểm tra tính hợp lệ (tên danh mục không được trùng lặp) và lưu vào hệ thống, sau đó chuyển hướng về trang danh sách.

##### 3. Chỉnh sửa danh mục:
- **Bước 1:** Quản lý chọn danh mục cần chỉnh sửa từ bảng danh sách và nhấn nút "Sửa".
- **Bước 2:** Hệ thống mở biểu mẫu chứa sẵn thông tin hiện tại của danh mục. Quản lý cập nhật lại Tên danh mục hoặc Mô tả.
- **Bước 3:** Quản lý nhấn "Cập nhật". Hệ thống lưu thay đổi và hiển thị thông báo cập nhật thành công.

##### 4. Xóa danh mục:
- **Điều kiện:** Danh mục chỉ được phép xóa khi không chứa bất kỳ sản phẩm nào bên trong.
- **Bước 1:** Quản lý nhấn nút "Xóa" tại dòng danh mục tương ứng.
- **Bước 2:** Hệ thống hiển thị hộp thoại xác nhận yêu cầu xóa.
- **Bước 3:** Nếu danh mục đã gắn với sản phẩm, hệ thống từ chối xóa và hiển thị cảnh báo; nếu danh mục rỗng, hệ thống tiến hành xóa khỏi danh sách.

---

#### b.3. Quản lý sản phẩm & tồn kho (dành cho quản lý):

##### 1. Xem và Tìm kiếm / Lọc sản phẩm:
- **Bước 1:** Quản lý truy cập vào mục Quản lý Sản phẩm.
- **Bước 2:** Hệ thống hiển thị bảng danh sách sản phẩm phân trang gồm: Hình ảnh, Tên sản phẩm, Danh mục, Đơn giá niêm yết, Số lượng tồn kho và các nút chức năng (Sửa, Xóa).
- **Bước 3:** Quản lý có thể nhập từ khóa tên hàng vào ô tìm kiếm hoặc chọn lọc theo từng Danh mục để tra cứu nhanh.

##### 2. Thêm mới sản phẩm:
- **Bước 1:** Quản lý nhấn chọn nút "Thêm sản phẩm mới".
- **Bước 2:** Quản lý điền đầy đủ các thông tin: Tên sản phẩm, Chọn danh mục trực thuộc từ danh sách thả xuống, Đơn giá bán (phải lớn hơn 0), Số lượng nhập kho ban đầu ($\ge 0$), Đường dẫn ảnh đại diện, Mô tả công dụng và thành phần.
- **Bước 3:** Quản lý nhấn "Lưu sản phẩm". Hệ thống kiểm tra dữ liệu hợp lệ và ghi nhận sản phẩm mới vào danh mục kinh doanh.

##### 3. Chỉnh sửa thông tin sản phẩm:
- **Bước 1:** Quản lý chọn sản phẩm cần sửa và nhấn nút "Sửa".
- **Bước 2:** Hệ thống mở biểu mẫu cho phép cập nhật: Tên sản phẩm, Đơn giá mới, Số lượng tồn kho sau kiểm kê, Danh mục, Ảnh và Mô tả.
- **Bước 3:** Quản lý nhấn "Cập nhật" để hoàn tất việc lưu dữ liệu mới.

##### 4. Xóa sản phẩm:
- **Bước 1:** Quản lý chọn sản phẩm cần gỡ bỏ và nhấn nút "Xóa".
- **Bước 2:** Hệ thống yêu cầu xác nhận thao tác xóa.
- **Bước 3:** Sau khi xác nhận, sản phẩm được loại bỏ khỏi danh mục bán hàng công khai.

---

#### b.4. Quản lý đơn hàng & cập nhật chi tiết đơn (dành cho quản lý):

##### 1. Tra cứu và Xem danh sách đơn hàng:
- **Bước 1:** Quản lý truy cập vào mục Quản lý Đơn hàng.
- **Bước 2:** Hệ thống hiển thị danh sách đơn hàng theo trình tự thời gian mới nhất, bao gồm: Mã đơn hàng, Tên người nhận, Số điện thoại, Địa chỉ giao hàng, Ngày đặt hàng, Tổng tiền thanh toán và Trạng thái xử lý.

##### 2. Xem chi tiết đơn hàng:
- **Bước 1:** Quản lý nhấn chọn "Xem chi tiết" tại một đơn hàng cụ thể.
- **Bước 2:** Hệ thống hiển thị toàn bộ hồ sơ đơn hàng: Thông tin khách hàng, Địa chỉ giao nhận, Phương thức thanh toán, Ghi chú đơn và Bảng danh sách chi tiết các mặt hàng trong đơn (Tên sản phẩm, Đơn giá mua, Số lượng đặt, Thành tiền từng dòng).

##### 3. Cập nhật số lượng sản phẩm trong đơn hàng:
- **Bối cảnh áp dụng:** Khi khách hàng liên hệ xin điều chỉnh tăng/giảm số lượng sản phẩm trước khi bưu tá lấy hàng đóng gói.
- **Bước 1:** Tại trang Chi tiết đơn hàng, quản lý nhập số lượng mới vào ô số lượng của dòng sản phẩm tương ứng.
- **Bước 2:** Quản lý nhấn nút "Cập nhật số lượng".
- **Bước 3:** Hệ thống kiểm tra số lượng hợp lệ ($> 0$), tự động tính lại thành tiền của dòng sản phẩm đó và tính lại Tổng tiền thanh toán của toàn bộ đơn hàng, sau đó lưu lại thông tin cập nhật vào hệ thống.

---

#### b.5. Quản lý khách hàng:
1. **Tiếp nhận thông tin khách hàng:** Tự động thu thập thông tin khách hàng (Họ tên, SĐT, Địa chỉ) thông qua các giao dịch đặt hàng thành công.
2. **Tra cứu lịch sử mua hàng:** Nhân viên quản trị có thể tra cứu theo số điện thoại khách hàng để kiểm tra các đơn hàng khách đã từng đặt, tổng giá trị đã mua sắm để hỗ trợ tư vấn chăm sóc khách hàng thân thiết.

---

#### b.6. Quản lý hóa đơn & Thống kê doanh thu:
1. **Thống kê đơn hàng theo ngày:** Quản lý chọn ngày cần xem báo cáo. Hệ thống tổng hợp số lượng đơn phát sinh trong ngày, tổng số lượng mỹ phẩm đã xuất bán và tổng doanh thu thu về.
2. **Thống kê theo tháng / Quý:** Hệ thống hỗ trợ tổng kết doanh thu toàn diện theo từng tháng, hỗ trợ chủ cửa hàng nắm bắt biến động doanh số và đánh giá các dòng mỹ phẩm bán chạy nhất.

---

### c. Quy định

#### c.1. Quy định về Bán hàng, Giỏ hàng & Thanh toán:
- **Hạn mức đặt hàng:** Mỗi khách hàng trong một lần thêm sản phẩm vào giỏ và đặt hàng chỉ được mua tối đa **10 sản phẩm/mã hàng** nhằm ngăn chặn hành vi gom hàng đầu cơ, đảm bảo cơ hội mua hàng đồng đều cho mọi khách hàng.
- **Giỏ hàng tạm thời:** Giỏ hàng của khách hàng được duy trì liên tục trong suốt phiên truy cập trình duyệt. Khi khách đóng trình duyệt hoặc hoàn tất đặt hàng, giỏ hàng sẽ được giải phóng.
- **Phương thức thanh toán:**
  1. *Thanh toán khi nhận hàng (COD):* Khách hàng kiểm tra hàng đúng niêm phong và thanh toán tiền mặt cho nhân viên giao hàng.
  2. *Chuyển khoản ngân hàng:* Khách hàng chuyển khoản theo cú pháp hiển thị trên màn hình xác nhận đơn: `[Mã đơn hàng] - [Số điện thoại]`.

*Bảng quy định chính sách thanh toán & giao nhận:*

| Yếu tố quy định | Nội dung chi tiết quy định |
| :--- | :--- |
| **Giá trị đơn hàng tối thiểu** | Không quy định giá trị tối thiểu (cho phép đặt từ 1 sản phẩm). |
| **Hạn mức số lượng tối đa** | Tối đa 10 đơn vị sản phẩm cho mỗi mặt hàng trong 1 đơn. |
| **Chính sách Miễn phí vận chuyển** | Đơn hàng có tổng tiền hàng $\ge 500.000$ VNĐ được miễn phí vận chuyển toàn quốc. |
| **Thời gian lưu đơn chờ xác nhận** | Tối đa 48 giờ đối với đơn chuyển khoản ngân hàng; quá 48 giờ chưa nhận được thanh toán, đơn sẽ tự động chuyển sang hủy. |

---

#### c.2. Quy định về Quản lý Sản phẩm & Danh mục:
- **Quy định về Danh mục:**
  - Tên danh mục là bắt buộc, có độ dài từ 3 đến 100 ký tự và không được trùng lặp với danh mục đã có.
  - Không cho phép xóa danh mục nếu danh mục đó đang chứa sản phẩm liên kết bên trong. Muốn xóa danh mục, quản trị viên phải chuyển toàn bộ sản phẩm sang danh mục khác hoặc xóa các sản phẩm đó trước.
- **Quy định về Sản phẩm:**
  - Tên sản phẩm phải rõ ràng, duy nhất trong cùng một phân loại thương hiệu.
  - Đơn giá sản phẩm bắt buộc phải lớn hơn 0 VNĐ ($> 0$).
  - Số lượng tồn kho ban đầu khi nhập hàng phải là số nguyên không âm ($\ge 0$).
  - Hình ảnh sản phẩm phải là đường dẫn URL hợp lệ dẫn tới tệp ảnh (.png, .jpg, .webp).

---

#### c.3. Quy định về Quản lý Đơn hàng & Cập nhật số lượng:
- **Tính toàn vẹn giá đơn hàng:** Giá sản phẩm ghi trên chi tiết đơn hàng là **giá tại thời điểm khách bấm đặt hàng**. Khi cửa hàng thay đổi giá niêm yết của sản phẩm sau này, giá trên các đơn hàng đã tạo trong quá khứ tuyệt đối không bị thay đổi.
- **Quy định điều chỉnh số lượng đơn hàng:**
  - Chỉ nhân viên quản trị có thẩm quyền mới được phép điều chỉnh số lượng sản phẩm trong đơn hàng.
  - Số lượng sau khi chỉnh sửa phải là số nguyên dương ($> 0$). Trường hợp khách muốn hủy hẳn món hàng đó khỏi đơn, phải thực hiện xóa dòng chi tiết tương ứng.
  - Sau khi thay đổi số lượng, hệ thống bắt buộc phải tính toán lại thành tiền từng dòng và cập nhật lại Tổng tiền thanh toán của toàn bộ đơn hàng.

---

#### c.4. Quy định về Quản lý Khách hàng & Giao nhận:
- **Tính hợp lệ thông tin người nhận:**
  - Họ và tên người nhận: Không được để trống, độ dài từ 2 đến 50 ký tự chữ cái.
  - Số điện thoại: Phải là số điện thoại di động hợp lệ tại Việt Nam (gồm 10 chữ số, bắt đầu bằng đầu số 03, 05, 07, 08, 09).
  - Địa chỉ giao hàng: Phải ghi rõ số nhà, tên đường, phường/xã, quận/huyện, tỉnh/thành phố để phục vụ việc giao nhận chính xác.

---

#### c.5. Quy định về Phân quyền & Quản trị hệ thống:
- **Khách hàng vãng lai (Public User):** Có quyền truy cập xem trang chủ, duyệt danh mục, xem chi tiết sản phẩm, tìm kiếm mỹ phẩm, quản lý giỏ hàng cá nhân và thực hiện đặt hàng.
- **Quản trị viên (Admin):** Phải đăng nhập bằng tài khoản và mật khẩu bảo mật được cấp. Có toàn quyền truy cập phân hệ Quản lý Danh mục, Quản lý Sản phẩm, Quản lý Đơn hàng và Thống kê doanh thu.

---

#### c.6. Quy định về Quản lý Thống kê & Hóa đơn:
- Mọi đơn hàng sau khi hoàn tất thanh toán đều được lưu trữ vĩnh viễn trong cơ sở dữ liệu làm căn cứ đối soát kế toán và bảo hành sản phẩm.
- Báo cáo thống kê doanh thu là doanh thu gộp (Gross Revenue) phản ánh toàn bộ giá trị các đơn hàng thành công đã phát sinh.

---

## 2. Danh sách các câu hỏi khi thu thập và làm rõ yêu cầu của ứng dụng

| STT | Câu hỏi (Questions) | Trả lời (Answers) | Ghi chú |
| :---: | :--- | :--- | :--- |
| **1.** | Khách hàng của PinkyCloud chủ yếu là đối tượng nào và cửa hàng có yêu cầu đặc biệt gì về phong cách hiển thị hình ảnh, thông tin sản phẩm trên website? | Đối tượng khách hàng chính của PinkyCloud là giới trẻ (học sinh, sinh viên, nhân viên văn phòng trẻ tuổi từ 18–30 tuổi). Cửa hàng yêu cầu phong cách hiển thị phải trẻ trung, hiện đại, sử dụng các khối thẻ sản phẩm có đường viền đậm rõ ràng, màu sắc tương phản cao và nút bấm to bản để các mặt hàng mỹ phẩm nổi bật, giúp khách dễ phân biệt nhanh giữa các mã màu son, loại dung tích và thao tác mua sắm trên điện thoại không bị nhầm lẫn. | *Người hỏi: Trần Gia Hào* |
| **2.** | Về thói quen mua sắm của khách hàng qua thiết bị di động, cửa hàng đặt ra yêu cầu gì về tốc độ hiển thị và khả năng tiếp cận thông tin sản phẩm? | Đại đa số khách hàng truy cập website bằng điện thoại qua mạng di động (3G/4G). Cửa hàng yêu cầu trang web phải hiển thị đầy đủ hình ảnh, bảng giá và nội dung sản phẩm ngay lập tức khi vừa mở trang (thời gian dưới 1.5 giây), không để xảy ra tình trạng màn hình trắng chờ tải dữ liệu làm khách nản lòng rời đi, đồng thời thông tin sản phẩm phải dễ dàng tìm thấy trên các công cụ tìm kiếm của người dùng. | *Người hỏi: Phạm Thanh Huy* |
| **3.** | Khi hai khách hàng cùng truy cập và nhấn mua một sản phẩm mỹ phẩm cuối cùng trong kho tại cùng một thời điểm, cửa hàng muốn hệ thống xử lý như thế nào? | Hệ thống lưu giữ hàng trong giỏ mua sắm tạm thời của khách và chưa trừ tồn kho ngay để tránh giữ ảo hàng hóa. Việc trừ tồn kho chỉ diễn ra khi khách hàng nhấn nút "Xác nhận Đặt hàng". Đơn hàng của khách nào gửi yêu cầu xác nhận trước sẽ được tạo thành công và trừ kho; khách hàng gửi sau sẽ nhận được thông báo mặt hàng đã hết để đảm bảo tính công bằng và chính xác tồn kho thực tế. | *Người hỏi: Trình Hoàng Kỳ* |
| **4.** | Khi khách hàng đã đặt hàng trực tuyến nhưng sau đó gọi điện yêu cầu đổi số lượng (tăng hoặc giảm số lượng món hàng), nhân viên xử lý ra sao trên hệ thống? | Quản trị viên chỉ cần truy cập vào mục "Quản lý Đơn hàng", chọn xem chi tiết đơn hàng đó và nhập số lượng mới vào ô số lượng của dòng sản phẩm mong muốn rồi bấm "Cập nhật". Hệ thống sẽ tự động tính lại thành tiền của dòng sản phẩm đó và tính lại tổng tiền thanh toán của toàn bộ đơn hàng mà không cần phải hủy bỏ đơn đi lập lại từ đầu. | *Người hỏi: Nguyễn Công Huy* |
| **5.** | Khi thêm mới một sản phẩm mỹ phẩm vào hệ thống, các ràng buộc dữ liệu bắt buộc nào phải được kiểm tra? | Cửa hàng yêu cầu kiểm tra chặt chẽ: Tên sản phẩm không được để trống; Phải chọn một danh mục cha hợp lệ; Đơn giá bán phải là số tiền lớn hơn 0 VNĐ; Số lượng tồn kho ban đầu khi nhập hàng phải là số không âm ($\ge 0$); Hình ảnh đại diện phải rõ ràng và phần mô tả phải có đầy đủ thông tin về công dụng, thành phần để khách hàng yên tâm mua sắm. | *Người hỏi: Trần Gia Hào* |
| **6.** | Cửa hàng có áp dụng chính sách giới hạn số lượng mua tối đa cho mỗi mặt hàng trong một đơn hàng không? | Có. Nhằm tránh hiện tượng các đối tượng ôm hàng số lượng lớn trong các đợt mở bán mỹ phẩm hot hoặc các chương trình giảm giá sâu làm hết hàng của khách lẻ khác, cửa hàng quy định mỗi lần mua một mã sản phẩm tối đa là 10 đơn vị. | *Người hỏi: Trình Hoàng Kỳ* |
| **7.** | Hệ thống quản lý danh mục có cho phép xóa một danh mục đang chứa các sản phẩm mỹ phẩm bên trong không? | Tuyệt đối không. Để đảm bảo không làm mất thông tin hàng hóa, nếu một danh mục đang có sản phẩm liên kết thì hệ thống sẽ chặn thao tác xóa và hiển thị cảnh báo yêu cầu quản trị viên phải di chuyển hoặc xóa các sản phẩm trực thuộc trước khi xóa danh mục đó. | *Người hỏi: Phạm Thanh Huy* |
| **8.** | Khi giá niêm yết của một loại mỹ phẩm trên trang chủ được quản trị viên điều chỉnh tăng hoặc giảm giá, các đơn hàng đã đặt trong quá khứ có bị thay đổi giá theo không? | Không. Mọi đơn hàng khi được tạo thành công sẽ lưu giữ cố định giá bán của từng sản phẩm tại đúng thời điểm giao dịch phát sinh (đóng băng giá lịch sử), việc thay đổi giá niêm yết hiện tại chỉ có hiệu lực với các đơn hàng tạo mới sau này. | *Người hỏi: Nguyễn Công Huy* |

---

## 3. Yêu cầu chức năng / phi chức năng của ứng dụng

### Yêu cầu chức năng:

#### Phân hệ Khách hàng (Storefront):
- **Xem Trang chủ & Sản phẩm nổi bật:** Hiển thị giao diện giới thiệu thương hiệu PinkyCloud, các danh mục mỹ phẩm tiêu biểu, danh sách sản phẩm mới nhất và sản phẩm đang bán chạy.
- **Xem Sản phẩm theo Danh mục:** Lọc và hiển thị danh sách sản phẩm theo từng nhóm chuyên biệt (Chăm sóc da, Trang điểm, Chăm sóc cơ thể...).
- **Xem Chi tiết Sản phẩm:** Hiển thị chi tiết hình ảnh đại diện sắc nét, tên sản phẩm, thương hiệu, đơn giá bán, tình trạng tồn kho và công dụng/thành phần hướng dẫn sử dụng.
- **Tìm kiếm Sản phẩm:** Cho phép nhập từ khóa tìm kiếm theo tên mỹ phẩm hoặc thương hiệu để tra cứu nhanh.
- **Quản lý Giỏ hàng Mua sắm:** Thêm sản phẩm vào giỏ hàng, cập nhật số lượng mua (từ 1 đến 10), xóa từng món hàng hoặc xóa toàn bộ giỏ hàng; tự động tính toán tổng tiền tạm tính.
- **Tiến hành Đặt hàng & Thanh toán:** Nhập thông tin người nhận hàng (Họ tên, SĐT, Địa chỉ chi tiết, Ghi chú), lựa chọn phương thức thanh toán (COD, Chuyển khoản) và xác nhận gửi đơn hàng.

#### Phân hệ Quản trị (Admin Operations):
- **Quản lý Danh mục:**
  - Xem danh sách toàn bộ các danh mục mỹ phẩm.
  - Thêm mới danh mục với tên và mô tả hợp lệ.
  - Chỉnh sửa thông tin danh mục hiện có.
  - Xóa danh mục khi không chứa sản phẩm liên kết.
- **Quản lý Sản phẩm:**
  - Xem danh sách sản phẩm có phân trang, lọc theo danh mục và tìm kiếm từ khóa.
  - Thêm mới sản phẩm mỹ phẩm với đầy đủ hình ảnh, giá bán, số lượng tồn, danh mục cha và mô tả.
  - Cập nhật thông tin sản phẩm, điều chỉnh giá bán và cập nhật tồn kho sau kiểm kê.
  - Xóa sản phẩm ra khỏi danh mục kinh doanh.
- **Quản lý Đơn hàng:**
  - Xem danh sách toàn bộ các đơn hàng theo trình tự thời gian mới nhất.
  - Xem chi tiết từng đơn hàng (thông tin người nhận, danh sách từng sản phẩm đặt mua, đơn giá mua, thành tiền).
  - Cập nhật số lượng sản phẩm trong đơn hàng theo thỏa thuận xuất kho thực tế, tự động tính lại tổng tiền thanh toán của đơn hàng.
- **Quản lý Thống kê & Báo cáo:**
  - Thống kê doanh thu theo ngày, tháng.
  - Theo dõi số lượng đơn hàng đã xử lý và số lượng sản phẩm tiêu thụ.
- **Phân quyền truy cập hệ thống:** Phân định quyền truy cập công khai cho khách hàng và quyền bảo mật đăng nhập dành riêng cho Quản trị viên.

---

### Yêu cầu phi chức năng:
- **Hiệu năng & Khả năng đáp ứng (Performance):** Thời gian phản hồi và nạp trang hoàn chỉnh đạt dưới 1.5 giây trong điều kiện mạng tiêu chuẩn. Nội dung và hình ảnh sản phẩm hiển thị ngay lập tức khi mở trang, đảm bảo hệ thống phục vụ mượt mà trong các khung giờ cao điểm có lượng truy cập lớn.
- **Tính chính xác (Accuracy):** Đảm bảo dữ liệu tính toán tiền hàng, chi phí vận chuyển và số lượng sản phẩm trong giỏ hàng cũng như đơn hàng luôn chính xác 100%, không xảy ra sai lệch số học.
- **Tính bảo mật (Security):** Khu vực quản trị nội bộ được bảo vệ bằng tài khoản và mật khẩu bảo mật, ngăn chặn các truy cập trái phép. Kiểm tra và xác thực toàn bộ dữ liệu nhập liệu từ người dùng (tên, số điện thoại, số lượng, giá tiền) để ngăn ngừa các sai sót nghiệp vụ và gian lận giá bán.
- **Tính dễ sử dụng & Trải nghiệm người dùng (Usability & User Experience):** Giao diện thiết kế theo phong cách khối hộp hiện đại, đường nét sắc nét, độ tương phản cao giúp các sản phẩm mỹ phẩm nổi bật, nút bấm kích thước lớn dễ thao tác trên màn hình cảm ứng điện thoại, hoàn toàn tương thích và hiển thị chuẩn mực trên mọi kích thước màn hình từ máy tính đến điện thoại di động.
- **Tính toàn vẹn dữ liệu (Data Integrity):** Giữ nguyên lịch sử giá bán của sản phẩm trên các đơn hàng đã tạo trong quá khứ, không bị ảnh hưởng khi giá niêm yết hiện tại thay đổi. Đảm bảo ràng buộc toàn vẹn giữa danh mục và sản phẩm trực thuộc.

---

## 4. Sơ đồ phân cấp chức năng của ứng dụng

Hệ thống Quản lý và Kinh doanh Mỹ phẩm Trực tuyến PinkyCloud được tổ chức thành 4 khối chức năng chính như sau:

```
HỆ THỐNG QUẢN LÝ & BÁN MỸ PHẨM PINKYCLOUD
│
├── 1. PHÂN HỆ MUA SẮM TRỰC TUYẾN (STOREFRONT)
│   ├── 1.1. Xem Trang chủ & Sản phẩm nổi bật
│   ├── 1.2. Xem Sản phẩm theo Danh mục (Skincare, Makeup, Body care...)
│   ├── 1.3. Xem Chi tiết Sản phẩm & Thành phần
│   ├── 1.4. Tìm kiếm Sản phẩm theo từ khóa
│   ├── 1.5. Quản lý Giỏ hàng (Thêm, Sửa số lượng, Xóa món, Tính tạm tính)
│   └── 1.6. Đặt hàng & Thanh toán (Nhập thông tin giao nhận, Chọn COD/CK, Tạo đơn)
│
├── 2. PHÂN HỆ QUẢN TRỊ DANH MỤC & SẢN PHẨM (ADMIN PRODUCT & CATEGORY)
│   ├── 2.1. Quản lý Danh mục (Xem danh sách, Thêm mới, Chỉnh sửa, Xóa danh mục rỗng)
│   └── 2.2. Quản lý Sản phẩm (Xem danh sách phân trang, Thêm mới, Sửa giá/tồn, Xóa sản phẩm, Tìm kiếm/Lọc)
│
├── 3. PHÂN HỆ QUẢN TRỊ ĐƠN HÀNG (ADMIN ORDER MANAGEMENT)
│   ├── 3.1. Xem Danh sách Đơn hàng theo thời gian
│   ├── 3.2. Xem Chi tiết Đơn hàng & Danh sách mặt hàng
│   └── 3.3. Cập nhật Số lượng Sản phẩm trong Đơn hàng & Tự động tính lại Tổng tiền
│
└── 4. PHÂN HỆ BẢO MẬT & BÁO CÁO THỐNG KÊ (SECURITY & REPORTS)
    ├── 4.1. Xác thực & Phân quyền Quản trị viên
    └── 4.2. Thống kê Doanh thu & Tổng hợp Số lượng Đơn hàng theo Ngày/Tháng
```

---

## 5. Các chức năng chính cho ứng dụng (Mục tiêu của ứng dụng)

### Quản lý Bán hàng & Giỏ hàng trực tuyến:
- Tiếp nhận và xử lý nhu cầu duyệt hàng, tìm kiếm mỹ phẩm của khách hàng một cách tự động 24/7.
- Cung cấp giao diện Giỏ hàng trực quan, hỗ trợ thay đổi số lượng và tính tiền tạm tính tức thời.
- Thu thập đầy đủ thông tin người nhận, ghi nhận đơn hàng vào hệ thống với đầy đủ mã đơn và thời gian đặt hàng chuẩn xác.

### Quản lý Danh mục Hàng hóa:
- Chuẩn hóa việc phân loại các nhóm mỹ phẩm thành các danh mục chuyên biệt (Chăm sóc da, Trang điểm, Chăm sóc cơ thể).
- Cho phép quản trị viên thêm, sửa, xóa các danh mục một cách linh hoạt, bảo đảm an toàn dữ liệu danh mục khi có sản phẩm liên kết.

### Quản lý Sản phẩm & Tồn kho:
- Số hóa toàn bộ thông tin chi tiết của từng sản phẩm mỹ phẩm (tên hàng, đơn giá, số lượng tồn kho, hình ảnh, công dụng, thành phần).
- Hỗ trợ công cụ tìm kiếm và lọc danh mục nhanh chóng cho ban quản lý.
- Cập nhật số lượng tồn kho và điều chỉnh đơn giá bán một cách nhanh chóng, tránh sai lệch dữ liệu giữa sổ sách và thực tế kệ hàng.

### Quản lý Đơn hàng & Chi tiết Đơn:
- Cung cấp công cụ theo dõi danh sách toàn bộ các đơn hàng phát sinh từ kênh trực tuyến.
- Hiển thị chi tiết từng dòng sản phẩm trong đơn, cho phép quản trị viên chủ động điều chỉnh số lượng hàng thực xuất theo yêu cầu của khách hàng trước khi giao cho đơn vị vận chuyển.
- Tự động tính toán lại thành tiền từng món và tổng giá trị đơn hàng một cách minh bạch, chính xác tuyệt đối.

### Quản lý Hóa đơn & Báo cáo Doanh thu:
- Lưu trữ toàn bộ lịch sử giao dịch bán lẻ làm căn cứ đối soát tài chính.
- Hỗ trợ tổng kết doanh thu và thống kê sản lượng tiêu thụ theo ngày, tháng, giúp người quản lý nắm bắt tình hình kinh doanh để xây dựng kế hoạch nhập hàng và khuyến mãi tối ưu.
