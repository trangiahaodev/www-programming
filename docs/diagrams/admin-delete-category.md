# Mermaid Diagrams: admin-delete-category

Tài liệu thiết kế kiến trúc và luồng xử lý chi tiết cho Use Case **Quản trị viên xóa danh mục mỹ phẩm** (`uc001c-admin-delete-category`).

---

## 1. System Architecture

Biểu diễn luồng phân lớp Server-Side Rendering (SSR) trong quy trình xóa danh mục mỹ phẩm từ trình duyệt của Quản trị viên qua bộ lọc bảo mật Spring Security, Spring MVC Controller, Service Layer (quản lý logic kiểm tra ràng buộc sản phẩm), Spring Data JPA Repository tới cơ sở dữ liệu Microsoft SQL Server.

```mermaid
flowchart TD
    Client["Browser (Admin Client)"]
    Modal["Danger Confirmation Modal<br>(Custom Dialog)"]
    Security["Spring Security Filter Chain<br>(CSRF Protection & ROLE_ADMIN)"]
    Controller["AdminCategoryController<br>(Spring MVC @Controller)"]
    Service["CategoryService / CategoryServiceImpl<br>(Service Layer @Transactional)"]
    Repository["CategoryRepository<br>(Spring Data JPA / Hibernate)"]
    Database[("Microsoft SQL Server<br>(Table: categories)")]
    View["Thymeleaf Engine<br>(admin/category-list.html)"]

    Client -->|"1. Click Delete button"| Modal
    Modal -->|"Cancel action"| Client
    Modal -->|"2. Submit POST /admin/categories/{id}/delete (CSRF Token)"| Security
    Security -->|"Authenticated & CSRF Valid"| Controller
    Controller -->|"3. deleteCategory(id)"| Service
    Service -->|"4. findById(id)"| Repository
    Repository -->|"Category entity"| Service
    Service -->|"5. Check associated products constraint"| Service
    Service -->|"6. delete(Category)"| Repository
    Repository -->|"SQL: DELETE FROM categories WHERE id=?"| Database
    Database -->|"Row deleted"| Repository
    Repository -->|"Transaction committed"| Service
    Service -->|"void success"| Controller
    Controller -->|"7. Redirect: GET /admin/categories (PRG Pattern)"| Client
    Client -->|"8. HTTP GET /admin/categories"| Controller
    Controller -->|"Render view with Flash Message"| View
    View -->|"Render HTML with Success/Error alert"| Client
```

---

## 2. Sequence Diagram

Mô tả chi tiết chu kỳ Request-Response theo chuẩn học thuật chuyên sâu, bao quát toàn bộ quy trình kích hoạt Modal cảnh báo nguy hiểm, kiểm tra bảo mật CSRF / quyền hạn, Alternate Flow (người dùng hủy thao tác xóa), và các Exception Flows (danh mục không tồn tại, danh mục đang chứa sản phẩm liên kết không được phép xóa, lỗi kết nối CSDL).

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Quản trị viên (Admin)
    participant Browser as Trình duyệt (Client)
    participant Modal as Danger Confirmation Modal
    participant Security as Spring Security Filter
    participant Controller as AdminCategoryController
    participant Service as CategoryServiceImpl
    participant Repository as CategoryRepository
    participant DB as SQL Server (categories)
    participant View as Thymeleaf (category-list.html)

    %% Giai đoạn 1: Kích hoạt cảnh báo xác nhận
    Note over Admin, Modal: Giai đoạn 1: Cảnh báo xác nhận an toàn trước khi xóa
    Admin->>Browser: Nhấn biểu tượng thùng rác (Xóa) tại một dòng danh mục
    Browser->>Modal: Kích hoạt hiển thị Danger Confirmation Modal
    Modal-->>Admin: Hiển thị tên danh mục & cảnh báo "Hành động này không thể hoàn tác!"

    alt 3.1 Quản trị viên nhấn nút "Hủy bỏ" hoặc nhấp ngoài Modal (Alternate Flow)
        Admin->>Modal: Nhấn nút "Hủy bỏ"
        Modal->>Modal: Đóng hộp thoại xác nhận
        Modal-->>Browser: Giữ nguyên trạng thái bảng danh sách
        Browser-->>Admin: Kết thúc thao tác xóa (Không gửi request lên Server)
    else 3. Quản trị viên nhấn "Xác nhận xóa" (Main Flow)
        Admin->>Modal: Nhấn nút "Xác nhận xóa"
        Modal->>Browser: Submit Form POST /admin/categories/{id}/delete kèm _csrf token
        
        %% Giai đoạn 2: Tiếp nhận và Xác thực tại Server
        Note over Browser, DB: Giai đoạn 2: Xử lý nghiệp vụ & Giao dịch CSDL
        Browser->>Security: HTTP POST /admin/categories/{id}/delete (_csrf, session cookie)
        
        alt 2.1 CSRF Token không hợp lệ hoặc hết hạn phiên
            Security-->>Browser: HTTP 403 Forbidden / Redirect 302 to /login
            Browser-->>Admin: Yêu cầu đăng nhập lại
        else Xác thực & Phân quyền ROLE_ADMIN thành công
            Security->>Controller: Forward request tới deleteCategory(id, redirectAttributes)
            activate Controller
            Controller->>Service: deleteCategory(id)
            activate Service
            
            %% Kiểm tra sự tồn tại của entity
            Service->>Repository: findById(id)
            activate Repository
            Repository->>DB: SELECT * FROM categories WHERE id = ?
            DB-->>Repository: Category entity (hoặc Optional.empty())
            deactivate Repository
            
            alt 5.1.1 Không tìm thấy danh mục theo ID (Exception Flow)
                Service-->>Controller: throw IllegalArgumentException("Không tìm thấy danh mục mỹ phẩm cần xóa!")
                Controller->>Controller: redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy danh mục...")
                Controller-->>Browser: HTTP 302 Redirect to /admin/categories
                deactivate Controller
                Browser->>Controller: HTTP GET /admin/categories
                activate Controller
                Controller->>View: render("admin/category-list", model với flash errorMessage)
                deactivate Controller
                activate View
                View-->>Browser: Render HTML hiển thị Flash Alert màu đỏ cảnh báo lỗi
                deactivate View
                Browser-->>Admin: Báo lỗi "Không tìm thấy danh mục cần xóa!"
            else Tìm thấy danh mục hợp lệ
                %% Kiểm tra ràng buộc sản phẩm
                Service->>Service: countProductsByCategoryId(id)
                
                alt 6.1.1 Danh mục đang có sản phẩm liên kết > 0 (Exception Flow)
                    Service-->>Controller: throw IllegalStateException("Không thể xóa danh mục vì đang có sản phẩm liên kết!")
                    activate Controller
                    Controller->>Controller: redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa danh mục đang có sản phẩm...")
                    Controller-->>Browser: HTTP 302 Redirect to /admin/categories
                    deactivate Controller
                    Browser->>Controller: HTTP GET /admin/categories
                    activate Controller
                    Controller->>View: render("admin/category-list", model với flash errorMessage)
                    deactivate Controller
                    activate View
                    View-->>Browser: Render HTML hiển thị Flash Alert màu đỏ cảnh báo ràng buộc dữ liệu
                    deactivate View
                    Browser-->>Admin: Báo lỗi không cho phép xóa danh mục đang có sản phẩm
                else Danh mục không chứa sản phẩm (productCount = 0) - Thỏa mãn điều kiện xóa
                    Service->>Repository: delete(category)
                    activate Repository
                    Repository->>DB: DELETE FROM categories WHERE id = ?
                    
                    alt 7.1.1 Lỗi kết nối CSDL hoặc ngoại lệ hệ thống
                        DB-->>Repository: DataAccessException / Connection Timeout
                        Repository-->>Service: throw Exception
                        Service-->>Controller: throw Exception (Rollback Transaction)
                        activate Controller
                        Controller->>Controller: redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống...")
                        Controller-->>Browser: HTTP 302 Redirect to /admin/categories
                        deactivate Controller
                    else Xóa bản ghi thành công
                        DB-->>Repository: 1 row affected (DELETE successful)
                        Repository-->>Service: Hoàn tất xóa
                        deactivate Repository
                        Service-->>Controller: void (Transaction Committed)
                        deactivate Service
                        
                        activate Controller
                        Controller->>Controller: redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục mỹ phẩm thành công!")
                        Controller-->>Browser: HTTP 302 Redirect to /admin/categories (PRG Pattern)
                        deactivate Controller
                        
                        Browser->>Controller: HTTP GET /admin/categories
                        activate Controller
                        Controller->>Service: getCategories(keyword, pageable)
                        Service->>Repository: searchCategories(...)
                        Repository->>DB: SELECT * FROM categories WHERE ...
                        DB-->>Repository: Danh sách categories đã loại bỏ bản ghi vừa xóa
                        Repository-->>Service: Page<Category>
                        Service-->>Controller: Page<CategoryResponseDTO>
                        Controller->>View: render("admin/category-list", model với flash successMessage)
                        deactivate Controller
                        activate View
                        View-->>Browser: Render HTML danh sách mới kèm Flash Alert màu xanh thành công
                        deactivate View
                        Browser-->>Admin: Hiển thị bảng danh mục đã cập nhật và thông báo thành công
                    end
                end
            end
        end
    end
```

---

## 3. Class Diagram

Mô hình cấu trúc lớp thể hiện mối quan hệ giữa thực thể CSDL `Category` và DTO hiển thị danh sách `CategoryResponseDTO` tham gia trong use case `admin-delete-category`.

```mermaid
classDiagram
    class Category {
        <<entity>>
        -String id
        -String categoryCode
        -String name
        -String description
        -Boolean active
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        +getId() String
        +getCategoryCode() String
        +getName() String
        +getDescription() String
        +getActive() Boolean
        +getCreatedAt() LocalDateTime
        +getUpdatedAt() LocalDateTime
        +setName(String name) void
        +setDescription(String desc) void
        +setActive(Boolean active) void
    }

    class CategoryResponseDTO {
        <<DTO>>
        -String id
        -String categoryCode
        -String name
        -String description
        -Boolean active
        -Long productCount
        -LocalDateTime createdAt
        +getId() String
        +getCategoryCode() String
        +getName() String
        +getDescription() String
        +getActive() Boolean
        +getProductCount() Long
        +getCreatedAt() LocalDateTime
    }

    Category ..> CategoryResponseDTO : mapped to response DTO for updated list rendering after deletion
```
