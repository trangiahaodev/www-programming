### 1. System Architecture
```mermaid
flowchart LR
    Browser --> Security[Spring Security LogoutFilter]
    Security --> Context[SecurityContextLogoutHandler]
    Security --> MVC[AuthController hiển thị login]
    MVC --> View[Thymeleaf]
```

### 2. Sequence Diagram
```mermaid
sequenceDiagram
    autonumber
    actor A as Customer hoặc Admin
    participant V as Trình duyệt
    participant S as Spring Security
    participant H as SecurityContextLogoutHandler
    participant C as AuthController
    alt 1.1–1.2 Không chọn Đăng xuất
        A->>V: Tiếp tục sử dụng chức năng khác
        Note over A,S: Không gửi yêu cầu logout
    else 1. Chọn Đăng xuất
        A->>V: Bấm nút Đăng xuất
        V->>S: POST /logout + CSRF
        alt 2.1.1–2.1.2 Token thiếu, sai hoặc hết hiệu lực
            S-->>V: 403, không xác nhận đăng xuất
            Note over A,V: Tải lại trang để xác định trạng thái
        else 2. Yêu cầu hợp lệ
            S->>H: logout(request, response, authentication)
            H->>H: Invalidate session, clear SecurityContext hiện tại
            H-->>S: Hoàn tất
            S-->>V: Redirect /login?logout
            V->>C: GET /login?logout
            C-->>V: guest/login
            V-->>A: Bạn đã đăng xuất
        end
    end
    Note over S,H: Logout do filter xử lý, không gọi UserService hay DELETE User
```

### 3. Class Diagram
```mermaid
classDiagram
    class ShopPrincipal {
        <<DTO>>
        String id
        String email
        String role
        boolean enabled
    }
```

Đăng xuất xóa thông tin xác thực của lần đăng nhập hiện tại. Không cập nhật entity, không có LogoutDTO hoặc lời gọi Repository riêng.
