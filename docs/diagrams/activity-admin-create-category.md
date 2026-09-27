# SƠ ĐỒ HOẠT ĐỘNG (ACTIVITY DIAGRAM)
## USE CASE: QUẢN TRỊ VIÊN THÊM MỚI DANH MỤC MỸ PHẨM (`uc001a-admin-create-category`)

---

## 1. GIỚI THIỆU & NGUYÊN TẮC MÔ HÌNH HÓA

Sơ đồ Hoạt động (Activity Diagram) dưới đây mô tả chi tiết chuỗi hành động và tương tác phân làn (Swimlane) giữa **Quản trị viên (Admin)** và **Hệ thống PinkyCloud** trong quá trình tạo mới một danh mục mỹ phẩm.

- **Nguồn chân lý (Source of Truth):** Đặc tả ca sử dụng chi tiết [`docs/usecases/uc001a-admin-create-category.md`](file:///E:/java-www/www-programming/docs/usecases/uc001a-admin-create-category.md).
- **Mức độ bao phủ:** Phản ánh chính xác 1-1 Luồng sự kiện chính (Main Flow - các bước 1 đến 7), Luồng thay thế (Alternate Flow - bước 3.1 đến 3.3) và toàn bộ các Luồng ngoại lệ (Exception Flows - 2.1.1, 4.1.1, 5.1.1, 5.2.1, 6.1.1) kèm các vòng lặp điều chỉnh dữ liệu.

---

## 2. SƠ ĐỒ SWIMLANE ACTIVITY DIAGRAM (MERMAID UML)

```mermaid
flowchart TD
    %% =========================================================================
    %% LÀN 1: QUẢN TRỊ VIÊN (ACTOR)
    %% =========================================================================
    subgraph Admin_Lane["Quản trị viên (Admin)"]
        Start(((Bắt đầu)))
        A1(["1. Nhấn nút 'Thêm mới danh mục' trên thanh menu hoặc trang danh sách"])
        A3(["3. Nhập thông tin danh mục:<br>• Mã danh mục (categoryCode)<br>• Tên danh mục (name)<br>• Mô tả (description)<br>• Trạng thái hoạt động (active)"])
        D_Action{"Quản trị viên chọn thao tác?"}
    end

    %% =========================================================================
    %% LÀN 2: HỆ THỐNG PINKYCLOUD (SYSTEM)
    %% =========================================================================
    subgraph System_Lane["Hệ thống PinkyCloud"]
        %% Giai đoạn 1: Mở form & Kiểm tra phiên
        D_Auth{"Phiên đăng nhập & quyền<br>ROLE_ADMIN hợp lệ?"}
        A2_1_2(["2.1.2. Chặn yêu cầu, chuyển hướng về /login<br>kèm thông báo hết hạn phiên/không có quyền"])
        End_Auth(((Kết thúc: Bị từ chối)))
        A2(["2. Tiếp nhận GET /admin/categories/create,<br>khởi tạo form rỗng & hiển thị màn hình tạo mới (active = true)"])

        %% Giai đoạn 2: Hủy bỏ hoặc Tiếp nhận submit
        A3_2(["3.2. Hủy bỏ thao tác nhập liệu,<br>không lưu dữ liệu & chuyển hướng về /admin/categories"])
        End_Cancel(((Kết thúc: Hủy thao tác)))
        
        %% Giai đoạn 3: Xác thực JSR-380 & Kiểm tra trùng lặp
        A4(["4. Tiếp nhận POST /admin/categories/create,<br>kiểm tra validation JSR-380 (@Valid CategoryCreateDTO)"])
        D_JSR380{"Dữ liệu hợp lệ theo chuẩn JSR-380?<br>(Mã không rỗng/space/<=20 ký tự,<br>Tên không rỗng/<=150 ký tự,<br>Mô tả <=500 ký tự)"}
        A4_1_2(["4.1.2. Giữ lại dữ liệu vừa nhập trên form<br>& hiển thị thông báo lỗi inline chi tiết màu đỏ"])

        A5(["5. Kiểm tra tính duy nhất của Mã danh mục<br>và Tên danh mục trong cơ sở dữ liệu"])
        D_CheckCode{"Mã danh mục (categoryCode)<br>đã tồn tại trong CSDL?"}
        A5_1_2(["5.1.2. Giữ lại dữ liệu form & hiển thị lỗi inline:<br>'Mã danh mục đã tồn tại trong hệ thống'"])
        
        D_CheckName{"Tên danh mục (name)<br>đã tồn tại trong CSDL?"}
        A5_2_2(["5.2.2. Giữ lại dữ liệu form & hiển thị lỗi inline:<br>'Tên danh mục đã tồn tại trong hệ thống'"])

        %% Giai đoạn 4: Ghi CSDL & Transaction
        A6(["6. Tạo mới Entity Category, lưu vào bảng categories<br>& thực thi Transaction CSDL"])
        D_TxResult{"Ghi CSDL & Transaction<br>thành công?"}
        A6_1_2(["6.1.2. Rollback Transaction, giữ lại form<br>& hiển thị thông báo lỗi hệ thống tổng thể"])

        A7(["7. Chuyển hướng Quản trị viên về /admin/categories<br>& hiển thị thông báo flash: 'Thêm mới danh mục mỹ phẩm thành công!'"])
        End_Success(((Kết thúc: Thành công)))
    end

    %% =========================================================================
    %% LUỒNG ĐIỀU KHIỂN VÀ RẼ NHÁNH (CONTROL FLOWS)
    %% =========================================================================
    Start --> A1
    A1 --> D_Auth

    %% Rẽ nhánh kiểm tra Auth (2.1.1)
    D_Auth -->|"Không hợp lệ (2.1.1)"| A2_1_2
    A2_1_2 --> End_Auth
    D_Auth -->|"Hợp lệ (ROLE_ADMIN)"| A2

    %% Mở form chuyển quyền cho Quản trị viên nhập liệu
    A2 --> A3
    A3 --> D_Action

    %% Rẽ nhánh thao tác người dùng (3.1 Alternate Flow)
    D_Action -->|"3.1. Nhấn nút 'Hủy bỏ'"| A3_2
    A3_2 --> End_Cancel
    D_Action -->|"Nhấn nút 'Lưu danh mục'"| A4

    %% Rẽ nhánh kiểm tra JSR-380 (4.1.1)
    A4 --> D_JSR380
    D_JSR380 -->|"Vi phạm ràng buộc (4.1.1)"| A4_1_2
    A4_1_2 -.->|"4.1.3. Quay lại bước 3 sửa lỗi"| A3
    D_JSR380 -->|"Dữ liệu hợp lệ"| A5

    %% Rẽ nhánh kiểm tra trùng Mã danh mục (5.1.1)
    A5 --> D_CheckCode
    D_CheckCode -->|"Đã tồn tại (5.1.1)"| A5_1_2
    A5_1_2 -.->|"5.1.3. Quay lại bước 3 sửa lỗi"| A3
    D_CheckCode -->|"Chưa tồn tại"| D_CheckName

    %% Rẽ nhánh kiểm tra trùng Tên danh mục (5.2.1)
    D_CheckName -->|"Đã tồn tại (5.2.1)"| A5_2_2
    A5_2_2 -.->|"5.2.3. Quay lại bước 3 sửa lỗi"| A3
    D_CheckName -->|"Chưa tồn tại (Hợp lệ hoàn toàn)"| A6

    %% Rẽ nhánh kiểm tra kết quả lưu CSDL (6.1.1)
    A6 --> D_TxResult
    D_TxResult -->|"Sự cố CSDL / Exception (6.1.1)"| A6_1_2
    A6_1_2 -.->|"6.1.3. Quay lại bước 3 thử lại"| A3
    D_TxResult -->|"Thành công (Main Flow)"| A7

    %% Kết thúc luồng chính
    A7 --> End_Success
```

---

## 3. BẢNG GIẢI THÍCH CHI TIẾT CÁC ĐIỂM RẼ NHÁNH & NGOẠI LỆ

| Điểm Rẽ Nhánh (Decision) | Điều Kiện Kiểm Tra | Hành Động Xử Lý & Phản Hồi Hệ Thống | Luồng Tương Ứng trong Đặc Tả |
| :--- | :--- | :--- | :--- |
| **`D_Auth`** | Kiểm tra quyền và tính hợp lệ của phiên làm việc. | **Không hợp lệ:** Chuyển hướng người dùng về trang `/login`, thông báo lỗi hết hạn phiên và chấm dứt tiến trình.<br>**Hợp lệ:** Mở form tạo mới rỗng kèm trạng thái kích hoạt mặc định `active = true`. | Ngoại lệ `2.1.1` $\rightarrow$ `2.1.2` $\rightarrow$ `2.1.3` |
| **`D_Action`** | Thao tác lựa chọn của Quản trị viên trên Form. | **Hủy bỏ (3.1):** Hủy toàn bộ nội dung đang nhập, không ghi nhận xuống CSDL, điều hướng về danh sách `/admin/categories`.<br>**Lưu danh mục:** Đóng gói payload gửi HTTP POST lên máy chủ. | Luồng thay thế `3.1` $\rightarrow$ `3.2` $\rightarrow$ `3.3` |
| **`D_JSR380`** | Kiểm tra cú pháp và độ dài chuỗi đầu vào theo tiêu chuẩn Bean Validation. | **Vi phạm (4.1.1):** Trả về view tạo mới, bảo lưu dữ liệu đã nhập, render thông báo lỗi màu đỏ ngay dưới từng ô input vi phạm; yêu cầu người dùng sửa đổi.<br>**Hợp lệ:** Tiến hành kiểm tra nghiệp vụ tầng Service. | Ngoại lệ `4.1.1` $\rightarrow$ `4.1.2` $\rightarrow$ `4.1.3` (Loop về Bước 3) |
| **`D_CheckCode`** | Kiểm tra trùng lặp trường `categoryCode` trong bảng `categories`. | **Đã tồn tại (5.1.1):** Hiển thị lỗi inline tại trường mã danh mục: *"Mã danh mục '[code]' đã tồn tại trong hệ thống. Vui lòng chọn mã khác."*<br>**Chưa tồn tại:** Tiếp tục bước kiểm tra trùng tên. | Ngoại lệ `5.1.1` $\rightarrow$ `5.1.2` $\rightarrow$ `5.1.3` (Loop về Bước 3) |
| **`D_CheckName`** | Kiểm tra trùng lặp trường `name` trong bảng `categories`. | **Đã tồn tại (5.2.1):** Hiển thị lỗi inline tại trường tên danh mục: *"Tên danh mục '[name]' đã tồn tại trong hệ thống. Vui lòng đặt tên khác."*<br>**Chưa tồn tại:** Chuyển sang lưu trữ bản ghi. | Ngoại lệ `5.2.1` $\rightarrow$ `5.2.2` $\rightarrow$ `5.2.3` (Loop về Bước 3) |
| **`D_TxResult`** | Kiểm tra tính toàn vẹn Transaction khi thực thi INSERT xuống CSDL. | **Lỗi CSDL (6.1.1):** Tự động Rollback Transaction, không lưu rác dữ liệu, hiển thị cảnh báo: *"Không thể lưu danh mục vào lúc này. Vui lòng thử lại sau."*<br>**Thành công:** Commit Transaction, chuyển hướng về `/admin/categories` kèm thông báo flash màu xanh. | Ngoại lệ `6.1.1` $\rightarrow$ `6.1.2` $\rightarrow$ `6.1.3` & Luồng chính Bước 6, 7 |
