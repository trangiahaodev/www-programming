# Mermaid Diagrams: admin-view-categories

Tài liệu thiết kế kiến trúc và luồng xử lý chi tiết cho Use Case **Quản trị viên xem danh sách các danh mục mỹ phẩm** (`uc001d-admin-view-categories`).

---

## 1. System Architecture

Biểu diễn luồng phân lớp Server-Side Rendering (SSR) từ trình duyệt người dùng qua tầng bảo mật Spring Security, Spring MVC Controller, Service Layer, Spring Data JPA Repository đến cơ sở dữ liệu Microsoft SQL Server.

```mermaid
flowchart TD
    Client["Browser (Admin Client)"]
    Security["Spring Security Filter Chain<br>(Role: ROLE_ADMIN)"]
    Controller["AdminCategoryController<br>(Spring MVC @Controller)"]
    Service["CategoryService / CategoryServiceImpl<br>(Service Layer @Transactional)"]
    Repository["CategoryRepository<br>(Spring Data JPA / Hibernate)"]
    Database[("Microsoft SQL Server<br>(Table: categories)")]
    View["Thymeleaf Engine<br>(admin/category-list.html)"]

    Client -->|"HTTP GET /admin/categories?keyword=&page=0&size=10"| Security
    Security -->|"Authenticated (ROLE_ADMIN)"| Controller
    Security -.->|"Unauthorized / Expired Session"| Client
    Controller -->|"getCategories(keyword, pageable)"| Service
    Service -->|"searchCategories(keyword, pageable)"| Repository
    Repository -->|"JPQL: SELECT c FROM Category c WHERE ..."| Database
    Database -->|"ResultSet (categories records & count)"| Repository
    Repository -->|"Page&lt;Category&gt;"| Service
    Service -->|"map(convertToResponseDTO) -> Page&lt;CategoryResponseDTO&gt;"| Controller
    Controller -->|"Model attributes (categories, pagination, keyword, size)"| View
    View -->|"Rendered HTML Response"| Client
```

---

## 2. Sequence Diagram

Mô tả chi tiết chu kỳ Request-Response với chuẩn học thuật cao, bao gồm Main Flow, Alternate Flows (xóa bộ lọc, rỗng kết quả, đổi kích thước trang) và Exception Flows (hết hạn phiên đăng nhập, lỗi hệ thống cơ sở dữ liệu).

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Quản trị viên (Admin)
    participant Browser as Trình duyệt (Client)
    participant Security as Spring Security
    participant Controller as AdminCategoryController
    participant Service as CategoryServiceImpl
    participant Repository as CategoryRepository
    participant DB as SQL Server (categories)
    participant View as Thymeleaf (category-list.html)

    Admin->>Browser: Nhập URL /admin/categories (hoặc kèm keyword, page, size)
    Browser->>Security: HTTP GET /admin/categories?keyword={keyword}&page={page}&size={size}
    
    alt 2.1.1 Không có quyền ROLE_ADMIN hoặc hết hạn phiên (Exception Flow)
        Security-->>Browser: Redirect 302 /login (kèm thông báo lỗi phiên làm việc)
        Browser-->>Admin: Hiển thị trang đăng nhập
    else Quản trị viên đã xác thực hợp lệ (Main Flow)
        Security->>Controller: Forward request tới listCategories(keyword, page, size, model)
        activate Controller

        Controller->>Controller: Chuẩn hóa pageNumber = max(0, page), pageSize, Sort.by(DESC, "createdAt")
        Controller->>Service: getCategories(keyword, pageable)
        activate Service

        Service->>Service: Xử lý keyword: trim() & null check
        Service->>Repository: searchCategories(searchKeyword, pageable)
        activate Repository

        alt 2.2.1 Lỗi kết nối Database / Lỗi hệ thống (Exception Flow)
            Repository->>DB: SELECT c FROM Category c WHERE ...
            DB-->>Repository: DataAccessException / DB Error
            Repository-->>Service: Throw DataAccessException
            Service-->>Controller: Throw Exception
            Controller->>View: render("error/500" hoặc model.errorMessage)
            View-->>Browser: Hiển thị thông báo "Không thể tải danh sách danh mục"
        else Truy vấn Database thành công (Main Flow)
            Repository->>DB: SELECT c FROM Category c WHERE (:kw IS NULL OR LOWER(c.name) LIKE ... OR LOWER(c.categoryCode) LIKE ...)
            activate DB
            DB-->>Repository: ResultSet (Record rows & Total Elements count)
            deactivate DB

            Repository-->>Service: Page<Category>
            deactivate Repository

            Service->>Service: Map Category sang CategoryResponseDTO bằng @Builder
            Service-->>Controller: Page<CategoryResponseDTO>
            deactivate Service

            Controller->>Controller: model.addAttribute("categories", page.getContent())<br>model.addAttribute("currentPage", page.getNumber())<br>model.addAttribute("totalPages", page.getTotalPages())<br>model.addAttribute("totalElements", page.getTotalElements())<br>model.addAttribute("size", pageSize)<br>model.addAttribute("keyword", keyword)

            alt 5.1 Không có danh mục nào khớp từ khóa / Danh sách rỗng (Alternate Flow)
                Controller->>View: render("admin/category-list", model với categories = [])
                activate View
                View-->>Browser: Render HTML kèm Empty State ("Không tìm thấy danh mục phù hợp")
                deactivate View
            else Tìm thấy danh mục (Main Flow / Alternate Flow 4.1 / 6.1)
                Controller->>View: render("admin/category-list", model)
                activate View
                View-->>Browser: Render HTML bảng dữ liệu danh mục & bộ phân trang
                deactivate View
            end

            deactivate Controller
            Browser-->>Admin: Hiển thị giao diện danh sách danh mục mỹ phẩm
        end
    end
```

---

## 3. Class Diagram

Mô hình cấu trúc lớp chi tiết giữa Entity và DTO tham gia trong use case `admin-view-categories`.

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

    Category ..> CategoryResponseDTO : mapped to in Service Layer via @Builder
```
