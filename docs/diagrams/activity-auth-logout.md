# Activity — Đăng xuất

Đặc tả: [uc008-auth-logout](../usecases/uc008-auth-logout.md).

Các nút hành động giữ số bước của đặc tả; nút quyết định diễn giải điều kiện rẽ nhánh tại bước tương ứng. Đây là bản đối chiếu Mermaid để vẽ lại bằng UML Activity trong Visual Paradigm theo hướng dẫn nhóm.

```mermaid
flowchart TD
    subgraph Actor ["Khách hàng hoặc Admin"]
        Start(((Bắt đầu)))
        n1(["1. Chọn Đăng xuất"])
        n1_1(["1.1. Tiếp tục dùng chức năng khác"])
        Choice{"Chọn thao tác?"}
    end
    subgraph System ["Hệ thống PinkyCloud"]
        n2(["2. Kết thúc lần đăng nhập hiện<br/>tại; mở Đăng nhập và báo đã đăng<br/>xuất"])
        n1_2(["1.2. Không thực hiện đăng xuất"])
        n2_1_1(["2.1.1. Yêu cầu đăng xuất không hợp<br/>lệ"])
        n2_1_2(["2.1.2. Từ chối; không báo thành<br/>công; cần tải lại trang"])
        Valid{"2. Yêu cầu hợp lệ?"}
        End(((Kết thúc)))
    end
    Start --> Choice
    Choice -->|"Đăng xuất"| n1
    Choice -->|"Tiếp tục dùng"| n1_1
    n1_1 --> n1_2
    n1_2 --> End
    n1 --> Valid
    Valid -->|"Có"| n2
    n2 --> End
    Valid -->|"Không"| n2_1_1
    n2_1_1 --> n2_1_2
    n2_1_2 --> End
```

## Đối chiếu bước

| Bước đặc tả | Nút Activity |
| --- | --- |
| 1 | `n1` |
| 2 | `n2` |
| 1.1 | `n1_1` |
| 1.2 | `n1_2` |
| 2.1.1 | `n2_1_1` |
| 2.1.2 | `n2_1_2` |
