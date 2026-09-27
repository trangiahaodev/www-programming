# Activity — Xóa người dùng

Đặc tả: [uc007c-admin-delete-user](../usecases/uc007c-admin-delete-user.md).

Các nút hành động giữ số bước của đặc tả; nút quyết định diễn giải điều kiện rẽ nhánh tại bước tương ứng. Đây là bản đối chiếu Mermaid để vẽ lại bằng UML Activity trong Visual Paradigm theo hướng dẫn nhóm.

```mermaid
flowchart TD
    subgraph Actor ["Admin"]
        Start(((Bắt đầu)))
        n1(["1. Chọn Xóa tại một User"])
        n3(["3. Kiểm tra User; xác nhận Xóa"])
        n3_1(["3.1. Chọn Hủy hoặc nhấn Escape"])
        n3_3(["3.3. Bấm Xóa lặp khi đang xử lý"])
        Choice{"Chọn thao tác trong hộp<br/>thoại?"}
        Repeat{"Bấm gửi lặp?"}
    end
    subgraph System ["Hệ thống PinkyCloud"]
        n2(["2. Mở hộp thoại đúng tên/mã, Hủy<br/>và Xóa; chưa xóa"])
        n4(["4. Kiểm tra quyền, xác nhận và các<br/>ràng buộc; xóa; về danh sách giữ<br/>bộ lọc, chỉnh trang và báo kết quả"])
        n3_2(["3.2. Đóng hộp thoại; trả focus;<br/>không gửi yêu cầu"])
        n3_4(["3.4. Chặn gửi lặp; tiếp tục bước 4"])
        n4_1_1(["4.1.1. Yêu cầu hoặc quyền không<br/>hợp lệ"])
        n4_1_2(["4.1.2. Không xóa; báo yêu cầu sai,<br/>yêu cầu đăng nhập lại hoặc từ chối<br/>truy cập"])
        n4_2_1(["4.2.1. User không còn tồn tại"])
        n4_2_2(["4.2.2. Giữ bộ lọc; báo không tìm<br/>thấy User"])
        n4_3_1(["4.3.1. Tự xóa hoặc xóa Admin cuối<br/>cùng"])
        n4_3_2(["4.3.2. Không xóa; giữ bộ lọc; báo<br/>quy tắc bị vi phạm"])
        n4_4_1(["4.4.1. Có đơn hàng ở bất kỳ trạng<br/>thái nào"])
        n4_4_2(["4.4.2. Không xóa; giữ bộ lọc; báo<br/>đã có đơn hàng"])
        n4_5_1(["4.5.1. Dữ liệu liên quan hoặc cập<br/>nhật đồng thời"])
        n4_5_2(["4.5.2. Không xóa; giữ bộ lọc; báo<br/>liên quan hoặc đang cập nhật"])
        Valid{"4. Quyền và yêu cầu hợp lệ?"}
        Lock{"4. Có thể xử lý tài khoản<br/>lúc này?"}
        Exists{"4. User còn tồn tại?"}
        Guard{"4. Được phép xóa tài khoản<br/>này?"}
        Orders{"4. Có bất kỳ đơn hàng nào?"}
        Deleted{"4. Xóa không vướng dữ liệu<br/>liên quan?"}
        End(((Kết thúc)))
    end
    Start --> n1
    n1 --> n2
    n2 --> Choice
    Choice -->|"Hủy hoặc Escape"| n3_1
    n3_1 --> n3_2
    n3_2 --> End
    Choice -->|"Xác nhận"| n3
    n3 --> Repeat
    Repeat -->|"Có"| n3_3
    n3_3 --> n3_4
    n3_4 --> Valid
    Repeat -->|"Không"| Valid
    Valid -->|"Không"| n4_1_1
    n4_1_1 --> n4_1_2
    n4_1_2 --> End
    Valid -->|"Có"| Lock
    Lock -->|"Không"| n4_5_1
    Lock -->|"Có"| Exists
    Exists -->|"Không"| n4_2_1
    n4_2_1 --> n4_2_2
    n4_2_2 --> End
    Exists -->|"Có"| Guard
    Guard -->|"Không"| n4_3_1
    n4_3_1 --> n4_3_2
    n4_3_2 --> End
    Guard -->|"Có"| Orders
    Orders -->|"Có"| n4_4_1
    n4_4_1 --> n4_4_2
    n4_4_2 --> End
    Orders -->|"Không"| Deleted
    Deleted -->|"Không"| n4_5_1
    n4_5_1 --> n4_5_2
    n4_5_2 --> End
    Deleted -->|"Có"| n4
    n4 --> End
```

## Đối chiếu bước

| Bước đặc tả | Nút Activity |
| --- | --- |
| 1 | `n1` |
| 2 | `n2` |
| 3 | `n3` |
| 4 | `n4` |
| 3.1 | `n3_1` |
| 3.2 | `n3_2` |
| 3.3 | `n3_3` |
| 3.4 | `n3_4` |
| 4.1.1 | `n4_1_1` |
| 4.1.2 | `n4_1_2` |
| 4.2.1 | `n4_2_1` |
| 4.2.2 | `n4_2_2` |
| 4.3.1 | `n4_3_1` |
| 4.3.2 | `n4_3_2` |
| 4.4.1 | `n4_4_1` |
| 4.4.2 | `n4_4_2` |
| 4.5.1 | `n4_5_1` |
| 4.5.2 | `n4_5_2` |
