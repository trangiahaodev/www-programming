# Mermaid Diagrams: admin-create-category

Tài liệu thiết kế kiến trúc và luồng xử lý chi tiết cho Use Case **Quản trị viên thêm mới danh mục mỹ phẩm** (`uc001a-admin-create-category`).

---

## 1. System Architecture

Biểu diễn luồng phân lớp Server-Side Rendering (SSR) trong quy trình thêm mới danh mục mỹ phẩm từ trình duyệt của Quản trị viên qua bộ lọc Spring Security, Spring MVC Controller, Service Layer, Spring Data JPA Repository tới cơ sở dữ liệu Microsoft SQL Server.

```mermaid
flowchart TD
    Client["Browser (Admin Client)"]
    Security["Spring Security Filter Chain<br>(Role: ROLE_ADMIN)"]
    Controller["AdminCategoryController<br>(Spring MVC @Controller)"]
    Validation["Jakarta Bean Validation<br>(JSR-380 @Valid)"]
    Service["CategoryService / CategoryServiceImpl<br>(Service Layer @Transactional)"]
    Repository["CategoryRepository<br>(Spring Data JPA / Hibernate)"]
    Database[("Microsoft SQL Server<br>(Table: categories)")]
    View["Thymeleaf Engine<br>(admin/category-create.html)"]

    Client -->|"1. HTTP GET /admin/categories/create"| Security
    Security -->|"Authenticated (ROLE_ADMIN)"| Controller
    Controller -->|"Show form with empty CategoryCreateDTO"| View
    View -->|"Render HTML form"| Client

    Client -->|"2. HTTP POST /admin/categories/create (FormData)"| Security
    Security -->|"Authenticated"| Controller
    Controller -->|"Validate CategoryCreateDTO"| Validation
    Validation --"Has Errors (BindingResult)"--> Controller
    Controller --"Return View with Field Errors"--> View
    
    Validation --"Valid"--> Controller
    Controller -->|"createCategory(CategoryCreateDTO)"| Service
    Service -->|"existsByCategoryCode / existsByName"| Repository
    Service -->|"save(Category)"| Repository
    Repository -->|"SQL: INSERT INTO categories ..."| Database
    Database -->|"Persisted record & Generated UUID"| Repository
    Repository -->|"Category entity"| Service
    Service -->|"CategoryResponseDTO"| Controller
    Controller -->|"Redirect: /admin/categories (PRG Pattern)"| Client
```

---

## 2. Sequence Diagram

Mô tả chi tiết chu kỳ Request-Response theo chuẩn học thuật chuyên sâu, bao quát toàn bộ Main Flow, Alternate Flow (hủy bỏ) và các Exception Flows (lỗi xác thực JSR-380, trùng mã danh mục, trùng tên danh mục, lỗi cơ sở dữ liệu/transaction rollback).

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
    participant View as Thymeleaf (category-create.html)

    %% Giai đoạn 1: Mở form tạo mới
    Note over Admin, View: Giai đoạn 1: Truy cập và hiển thị Form tạo mới danh mục
    Admin->>Browser: Nhấn nút "Thêm mới danh mục"
    Browser->>Security: HTTP GET /admin/categories/create
    
    alt 2.1.1 Hết hạn phiên đăng nhập hoặc không có ROLE_ADMIN (Exception Flow)
        Security-->>Browser: Redirect 302 /login (Thông báo hết hạn phiên)
        Browser-->>Admin: Hiển thị trang đăng nhập
    else Xác thực thành công (Main Flow)
        Security->>Controller: Forward request tới showCreateForm(model)
        activate Controller
        Controller->>Controller: model.addAttribute("categoryDTO", new CategoryCreateDTO())
        Controller->>View: render("admin/category-create", model)
        deactivate Controller
        activate View
        View-->>Browser: Render HTML form tạo mới danh mục
        deactivate View
        Browser-->>Admin: Hiển thị form nhập liệu (Mã, Tên, Mô tả, Trạng thái)
    end

    %% Giai đoạn 2: Submit form hoặc Hủy bỏ
    Note over Admin, View: Giai đoạn 2: Nhập liệu và Gửi yêu cầu lưu danh mục
    alt 3.1 Quản trị viên nhấn nút "Hủy bỏ" / "Quay lại" (Alternate Flow)
        Admin->>Browser: Nhấn "Hủy bỏ"
        Browser->>Controller: HTTP GET /admin/categories
        Controller-->>Browser: Redirect 302 /admin/categories (Không lưu dữ liệu)
        Browser-->>Admin: Hiển thị lại trang danh sách danh mục
    else Quản trị viên nhập thông tin và nhấn "Lưu danh mục" (Main Flow)
        Admin->>Browser: Điền Form (categoryCode, name, description, active) & Nhấn "Lưu danh mục"
        Browser->>Controller: HTTP POST /admin/categories/create (CategoryCreateDTO)
        activate Controller

        %% Kiểm tra Validation JSR-380
        Controller->>Controller: Kiểm tra @Valid CategoryCreateDTO (BindingResult)

        alt 4.1.1 Dữ liệu không hợp lệ theo JSR-380: mã/tên rỗng, độ dài sai, ký tự đặc biệt (Exception Flow)
            Controller->>View: render("admin/category-create", categoryDTO, bindingResult)
            activate View
            View-->>Browser: Render form kèm thông báo lỗi inline đỏ tại từng trường vi phạm
            deactivate View
            Browser-->>Admin: Hiển thị lỗi nhập liệu để người dùng sửa đổi
        else Dữ liệu form hợp lệ theo JSR-380 (Main Flow)
            Controller->>Service: createCategory(CategoryCreateDTO)
            activate Service

            %% Kiểm tra trùng mã
            Service->>Repository: existsByCategoryCode(categoryCode)
            activate Repository
            Repository->>DB: SELECT COUNT(1) FROM categories WHERE category_code = ?
            DB-->>Repository: count
            Repository-->>Service: boolean existsCode
            deactivate Repository

            alt 5.1.1 Mã danh mục đã tồn tại trong hệ thống (Exception Flow)
                Service-->>Controller: throw IllegalArgumentException("Mã danh mục '[code]' đã tồn tại...")
                Controller->>View: render("admin/category-create", model.errorMessage)
                activate View
                View-->>Browser: Render form giữ lại dữ liệu + thông báo lỗi trùng mã
                deactivate View
                Browser-->>Admin: Hiển thị cảnh báo trùng mã danh mục
            else Mã danh mục hợp lệ & chưa tồn tại
                %% Kiểm tra trùng tên
                Service->>Repository: existsByName(name)
                activate Repository
                Repository->>DB: SELECT COUNT(1) FROM categories WHERE name = ?
                DB-->>Repository: count
                Repository-->>Service: boolean existsName
                deactivate Repository

                alt 5.2.1 Tên danh mục đã tồn tại trong hệ thống (Exception Flow)
                    Service-->>Controller: throw IllegalArgumentException("Tên danh mục '[name]' đã tồn tại...")
                    Controller->>View: render("admin/category-create", model.errorMessage)
                    activate View
                    View-->>Browser: Render form giữ lại dữ liệu + thông báo lỗi trùng tên
                    deactivate View
                    Browser-->>Admin: Hiển thị cảnh báo trùng tên danh mục
                else Tên danh mục chưa tồn tại (Hợp lệ hoàn toàn)
                    %% Thực hiện lưu Entity
                    Service->>Service: Map CategoryCreateDTO sang Category Entity (@Builder)
                    Service->>Repository: save(Category)
                    activate Repository
                    Repository->>DB: INSERT INTO categories (id, category_code, name, description, active, created_at, updated_at) VALUES (...)
                    activate DB

                    alt 6.1.1 Sự cố kết nối CSDL / Lỗi hệ thống (Exception Flow)
                        DB-->>Repository: DataAccessException / DB Error
                        deactivate DB
                        Repository-->>Service: throw Exception (Rollback Transaction)
                        Service-->>Controller: throw Exception
                        Controller->>View: render("admin/category-create", model.errorMessage)
                        activate View
                        View-->>Browser: Render form + thông báo lỗi hệ thống
                        deactivate View
                        Browser-->>Admin: Hiển thị thông báo "Không thể lưu danh mục vào lúc này"
                    else Ghi dữ liệu CSDL thành công & Commit Transaction (Main Flow)
                        activate DB
                        DB-->>Repository: Success (Generated UUID surrogate key)
                        deactivate DB
                        Repository-->>Service: Category (saved entity)
                        deactivate Repository

                        Service->>Service: Map Category sang CategoryResponseDTO
                        Service-->>Controller: CategoryResponseDTO
                        deactivate Service

                        Controller->>Controller: redirectAttributes.addFlashAttribute("successMessage", "Thêm mới danh mục mỹ phẩm thành công!")
                        Controller-->>Browser: HTTP 302 Redirect to /admin/categories (PRG Pattern)
                        deactivate Controller

                        Browser->>Controller: HTTP GET /admin/categories
                        activate Controller
                        Controller->>View: render("admin/category-list", model với successMessage)
                        deactivate Controller
                        activate View
                        View-->>Browser: Render HTML danh sách kèm Alert xanh "Thêm mới danh mục mỹ phẩm thành công!"
                        deactivate View
                        Browser-->>Admin: Hiển thị trang danh sách cập nhật danh mục mới
                    end
                end
            end
        end
    end
```

---

## 3. Class Diagram

Mô hình cấu trúc lớp chi tiết giữa Entity `Category`, `CategoryCreateDTO` và `CategoryResponseDTO` tham gia trong use case `admin-create-category`.

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

    class CategoryCreateDTO {
        <<DTO>>
        -String categoryCode
        -String name
        -String description
        -Boolean active
        +getCategoryCode() String
        +setCategoryCode(String code) void
        +getName() String
        +setName(String name) void
        +getDescription() String
        +setDescription(String desc) void
        +getActive() Boolean
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

    CategoryCreateDTO ..> Category : mapped to Category via Builder in Service Layer
    Category ..> CategoryResponseDTO : mapped to ResponseDTO via Builder in Service Layer
```
