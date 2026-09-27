# Activity — Cập nhật thông tin và trạng thái người dùng

Đặc tả: [uc007b-admin-update-user](../usecases/uc007b-admin-update-user.md).

Các nút hành động giữ số bước của đặc tả; nút quyết định diễn giải điều kiện rẽ nhánh tại bước tương ứng. Đây là bản đối chiếu Mermaid để vẽ lại bằng UML Activity trong Visual Paradigm theo hướng dẫn nhóm.

```mermaid
flowchart TD
    subgraph Actor ["Admin"]
        Start(((Bắt đầu)))
        n1(["1. Chọn Sửa người dùng"])
        n3(["3. Chỉnh thông tin/trạng thái;<br/>chọn Lưu"])
        n3_1(["3.1. Chọn Hủy"])
        Choice{"Chọn thao tác?"}
    end
    subgraph System ["Hệ thống PinkyCloud"]
        n2(["2. Kiểm tra quyền, tìm User; hiển<br/>thị biểu mẫu hiện tại"])
        n4(["4. Kiểm tra dữ liệu, email và quy<br/>tắc khóa; lưu thông tin; về danh<br/>sách và báo thành công"])
        n3_2(["3.2. Về danh sách; không lưu"])
        n2_1_1(["2.1.1. Chưa đăng nhập, tài khoản<br/>hết hiệu lực hoặc thiếu quyền"])
        n2_1_2(["2.1.2. Yêu cầu đăng nhập lại hoặc<br/>từ chối truy cập"])
        n2_2_1(["2.2.1. Không tìm thấy User khi mở"])
        n2_2_2(["2.2.2. Về danh sách; báo không tìm<br/>thấy"])
        n4_1_1(["4.1.1. Dữ liệu cập nhật không hợp<br/>lệ"])
        n4_1_2(["4.1.2. Giữ biểu mẫu; báo lỗi<br/>trường; trở lại bước 3"])
        n4_2_1(["4.2.1. Trùng email hoặc xung đột<br/>thông tin"])
        n4_2_2(["4.2.2. Không lưu; báo kiểm tra<br/>email; trở lại bước 3"])
        n4_3_1(["4.3.1. Tự khóa hoặc khóa Admin<br/>cuối cùng"])
        n4_3_2(["4.3.2. Không lưu; báo quy tắc bị<br/>vi phạm; trở lại bước 3"])
        n4_4_1(["4.4.1. User bị xóa trước khi lưu"])
        n4_4_2(["4.4.2. Về danh sách; báo không tìm<br/>thấy"])
        n4_5_1(["4.5.1. Yêu cầu gửi biểu mẫu không<br/>hợp lệ"])
        n4_5_2(["4.5.2. Từ chối yêu cầu; không lưu<br/>dữ liệu; kết thúc"])
        Access{"2. Được phép truy cập?"}
        Exists{"2. User tồn tại?"}
        Valid{"4. Dữ liệu hợp lệ?"}
        Still{"4. User còn tồn tại?"}
        Guard{"4. Được phép đổi trạng thái?"}
        Unique{"4. Email duy nhất và lưu<br/>không xung đột?"}
        Request{"4. Yêu cầu và quyền gửi còn<br/>hợp lệ?"}
        End(((Kết thúc)))
    end
    Start --> n1
    n1 --> Access
    Access -->|"Không"| n2_1_1
    n2_1_1 --> n2_1_2
    n2_1_2 --> End
    Access -->|"Có"| Exists
    Exists -->|"Không"| n2_2_1
    n2_2_1 --> n2_2_2
    n2_2_2 --> End
    Exists -->|"Có"| n2
    n2 --> Choice
    Choice -->|"Hủy"| n3_1
    n3_1 --> n3_2
    n3_2 --> End
    Choice -->|"Lưu"| n3
    n3 --> Request
    Valid -->|"Không"| n4_1_1
    n4_1_1 --> n4_1_2
    n4_1_2 --> n3
    Valid -->|"Có"| Still
    Still -->|"Không"| n4_4_1
    n4_4_1 --> n4_4_2
    n4_4_2 --> End
    Still -->|"Có"| Guard
    Guard -->|"Không"| n4_3_1
    n4_3_1 --> n4_3_2
    n4_3_2 --> n3
    Guard -->|"Có"| Unique
    Unique -->|"Không"| n4_2_1
    n4_2_1 --> n4_2_2
    n4_2_2 --> n3
    Unique -->|"Có"| n4
    n4 --> End
    Request -->|"Có"| Valid
    Request -->|"Không"| n4_5_1
    n4_5_1 --> n4_5_2
    n4_5_2 --> End
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
| 2.1.1 | `n2_1_1` |
| 2.1.2 | `n2_1_2` |
| 2.2.1 | `n2_2_1` |
| 2.2.2 | `n2_2_2` |
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
