### 1. System Architecture
```mermaid
flowchart LR
 Browser --> MVC[AdminUserController] --> Service[UserService] --> JPA[UserRepository] --> DB[(SQL Server)]
```
### 2. Sequence Diagram
```mermaid
sequenceDiagram
 actor Admin
 participant Security
 participant MVC as AdminUserController
 participant Service as UserServiceImpl
 participant Repo as UserRepository
 participant DB as SQL Server
 Admin->>Security: GET /admin/users?keyword&page&size
 alt Không có quyền ADMIN
 Security-->>Admin: Login hoặc 403
 else ADMIN hoạt động
 Security->>MVC: list(query)
 MVC->>MVC: @Valid UserSearchDTO
 alt Bộ lọc lỗi
 MVC->>Service: search(defaultQuery)
 else Bộ lọc hợp lệ
 MVC->>Service: search(query)
 end
 Service->>Repo: findByUserCodeContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(...)
 Repo->>DB: Truy vấn phân trang và count
 DB-->>Repo: Page User
 Repo-->>Service: Page User
 opt Trang vượt giới hạn sau khi xóa
 Service->>Repo: Truy vấn lại trang cuối
 end
 Service-->>MVC: Page UserResponseDTO (không password)
 MVC-->>Admin: Thymeleaf danh sách hoặc empty state
 end
```
### 3. Class Diagram
```mermaid
classDiagram
 class User { <<entity>> }
 class UserSearchDTO { <<DTO>>
 String keyword
 int page
 int size
 }
 class UserResponseDTO { <<DTO>>
 String id
 String userCode
 String fullName
 String email
 String phone
 String role
 Boolean active
 }
 User ..> UserResponseDTO : Service mapping
```
