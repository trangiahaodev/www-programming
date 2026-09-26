# Mermaid Diagrams: admin-update-category

Tài liệu thiết kế kiến trúc và luồng xử lý chi tiết cho Use Case **Quản trị viên cập nhật thông tin danh mục mỹ phẩm** (`uc001b-admin-update-category`).

---

## 1. System Architecture

Biểu diễn luồng phân lớp Server-Side Rendering (SSR) trong quy trình cập nhật danh mục mỹ phẩm từ trình duyệt của Quản trị viên qua bộ lọc Spring Security, Spring MVC Controller, Service Layer, Spring Data JPA Repository tới cơ sở dữ liệu Microsoft SQL Server.

```mermaid
flowchart TD
    Client["Browser (Admin Client)"]
    Security["Spring Security Filter Chain<br>(Role: ROLE_ADMIN)"]
    Controller["AdminCategoryController<br>(Spring MVC @Controller)"]
    Validation["Jakarta Bean Validation<br>(JSR-380 @Valid)"]
    Service["CategoryService / CategoryServiceImpl<br>(Service Layer @Transactional)"]
    Repository["CategoryRepository<br>(Spring Data JPA / Hibernate)"]
    Database[("Microsoft SQL Server<br>(Table: categories)")]
    View["Thymeleaf Engine<br>(admin/category-update.html)"]

    Client -->|"1. HTTP GET /admin/categories/{id}/edit"| Security
    Security -->|"Authenticated (ROLE_ADMIN)"| Controller
    Controller -->|"getCategoryForUpdate(id)"| Service
    Service -->|"findById(id)"| Repository
    Repository -->|"Category entity"| Service
    Service -->|"CategoryUpdateDTO"| Controller
    Controller -->|"Render edit form with CategoryUpdateDTO"| View
    View -->|"Render HTML edit form"| Client

    Client -->|"2. HTTP POST /admin/categories/{id}/edit (FormData)"| Security
    Security -->|"Authenticated"| Controller
    Controller -->|"Validate CategoryUpdateDTO"| Validation
    Validation --"Has Errors (BindingResult)"--> Controller
    Controller --"Return View with Field Errors"--> View
    
    Validation --"Valid"--> Controller
    Controller -->|"updateCategory(id, CategoryUpdateDTO)"| Service
    Service -->|"existsByNameAndIdNot(name, id)"| Repository
    Service -->|"save(Category)"| Repository
    Repository -->|"SQL: UPDATE categories SET name=?, description=?, active=?, updated_at=? WHERE id=?"| Database
    Database -->|"Updated record"| Repository
    Repository -->|"Category entity"| Service
    Service -->|"CategoryResponseDTO"| Controller
    Controller -->|"Redirect: /admin/categories (PRG Pattern)"| Client
```

---

## 2. Sequence Diagram

Mô tả chi tiết chu kỳ Request-Response theo chuẩn học thuật chuyên sâu, bao quát toàn bộ quy trình lấy dữ liệu form ban đầu, kiểm tra quyền, Alternate Flow (hủy bỏ) và các Exception Flows (không tìm thấy ID, lỗi xác thực JSR-380, trùng tên với danh mục khác, lỗi kết nối CSDL).

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
    participant View as Thymeleaf (category-update.html)

    %% Giai đoạn 1: Mở form chỉnh sửa
    Note over Admin, View: Giai đoạn 1: Lấy dữ liệu và Hiển thị Form chỉnh sửa
    Admin->>Browser: Nhấn nút "Chỉnh sửa" tại danh mục trên bảng danh sách
    Browser->>Security: HTTP GET /admin/categories/{id}/edit
    
    alt 2.1.1 Không có quyền ROLE_ADMIN hoặc hết hạn phiên (Exception Flow)
        Security-->>Browser: Redirect 302 /login (Thông báo hết hạn phiên)
        Browser-->>Admin: Hiển thị trang đăng nhập
    else Xác thực thành công (Main Flow)
        Security->>Controller: Forward request tới showEditForm(id, model)
        activate Controller
        Controller->>Service: getCategoryForUpdate(id)
        activate Service
        Service->>Repository: findById(id)
        activate Repository
        Repository->>DB: SELECT c FROM Category c WHERE c.id = ?
        DB-->>Repository: Category record (hoặc Optional.empty())
        deactivate Repository

        alt 2.1.1 Không tìm thấy danh mục theo ID (Exception Flow)
            Service-->>Controller: throw IllegalArgumentException("Không tìm thấy danh mục mỹ phẩm cần cập nhật!")
            Controller->>Controller: redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy danh mục...")
            Controller-->>Browser: HTTP 302 Redirect to /admin/categories
            Browser-->>Admin: Quay lại danh sách kèm thông báo lỗi
        else Tìm thấy danh mục hợp lệ (Main Flow)
            Service->>Service: Map Category sang CategoryUpdateDTO (khóa categoryCode)
            Service-->>Controller: CategoryUpdateDTO
            deactivate Service
            Controller->>Controller: model.addAttribute("categoryDTO", updateDTO)
            Controller->>View: render("admin/category-update", model)
            deactivate Controller
            activate View
            View-->>Browser: Render HTML form chỉnh sửa (Mã danh mục disabled / read-only)
            deactivate View
            Browser-->>Admin: Hiển thị form cập nhật danh mục
        end
    end

    %% Giai đoạn 2: Submit form hoặc Hủy bỏ
    Note over Admin, View: Giai đoạn 2: Cập nhật thông tin và Gửi yêu cầu lưu
    alt 3.1 Quản trị viên nhấn nút "Hủy bỏ" / "Quay lại" (Alternate Flow)
        Admin->>Browser: Nhấn "Hủy bỏ"
        Browser->>Controller: HTTP GET /admin/categories
        Controller-->>Browser: Redirect 302 /admin/categories (Không thay đổi dữ liệu)
        Browser-->>Admin: Hiển thị lại trang danh sách danh mục
    else Quản trị viên chỉnh sửa và nhấn "Cập nhật danh mục" (Main Flow)
        Admin->>Browser: Điền Form (name, description, active) & Nhấn "Cập nhật danh mục"
        Browser->>Controller: HTTP POST /admin/categories/{id}/edit (CategoryUpdateDTO)
        activate Controller

        %% Kiểm tra Validation JSR-380
        Controller->>Controller: Kiểm tra @Valid CategoryUpdateDTO (BindingResult)

        alt 4.1.1 Dữ liệu không hợp lệ theo JSR-380: tên rỗng, độ dài sai (Exception Flow)
            Controller->>View: render("admin/category-update", categoryDTO, bindingResult)
            activate View
            View-->>Browser: Render form kèm thông báo lỗi inline đỏ tại trường vi phạm
            deactivate View
            Browser-->>Admin: Hiển thị thông báo lỗi để Quản trị viên sửa đổi
        else Dữ liệu form hợp lệ theo JSR-380 (Main Flow)
            Controller->>Service: updateCategory(id, CategoryUpdateDTO)
            activate Service

            %% Tìm lại Category và kiểm tra trùng tên
            Service->>Repository: findById(id)
            activate Repository
            Repository->>DB: SELECT c FROM Category c WHERE c.id = ?
            DB-->>Repository: Category record
            Repository-->>Service: Category entity
            deactivate Repository

            Service->>Repository: existsByNameAndIdNot(name, id)
            activate Repository
            Repository->>DB: SELECT COUNT(1) FROM categories WHERE name = ? AND id <> ?
            DB-->>Repository: count
            Repository-->>Service: boolean existsDuplicateName
            deactivate Repository

            alt 5.1.1 Tên danh mục đã trùng với danh mục khác (Exception Flow)
                Service-->>Controller: throw IllegalArgumentException("Tên danh mục '[name]' đã tồn tại...")
                Controller->>View: render("admin/category-update", model.errorMessage)
                activate View
                View-->>Browser: Render form giữ lại dữ liệu + cảnh báo trùng tên
                deactivate View
                Browser-->>Admin: Hiển thị cảnh báo trùng tên danh mục
            else Tên danh mục hợp lệ (Không trùng lặp)
                %% Cập nhật Entity
                Service->>Service: category.setName(name)<br>category.setDescription(desc)<br>category.setActive(active)<br>(categoryCode KHÔNG thay đổi)
                Service->>Repository: save(category)
                activate Repository
                Repository->>DB: UPDATE categories SET name=?, description=?, active=?, updated_at=? WHERE id=?
                activate DB

                alt 6.1.1 Sự cố kết nối CSDL / Lỗi hệ thống (Exception Flow)
                    DB-->>Repository: DataAccessException / DB Error
                    deactivate DB
                    Repository-->>Service: throw Exception (Rollback Transaction)
                    Service-->>Controller: throw Exception
                    Controller->>View: render("admin/category-update", model.errorMessage)
                    activate View
                    View-->>Browser: Render form + thông báo lỗi hệ thống
                    deactivate View
                    Browser-->>Admin: Hiển thị thông báo "Không thể cập nhật danh mục vào lúc này"
                else Cập nhật CSDL thành công & Commit Transaction (Main Flow)
                    activate DB
                    DB-->>Repository: Success (Updated record)
                    deactivate DB
                    Repository-->>Service: Category (updated entity)
                    deactivate Repository

                    Service->>Service: Map Category sang CategoryResponseDTO
                    Service-->>Controller: CategoryResponseDTO
                    deactivate Service

                    Controller->>Controller: redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục mỹ phẩm thành công!")
                    Controller-->>Browser: HTTP 302 Redirect to /admin/categories (PRG Pattern)
                    deactivate Controller

                    Browser->>Controller: HTTP GET /admin/categories
                    activate Controller
                    Controller->>View: render("admin/category-list", model với successMessage)
                    deactivate Controller
                    activate View
                    View-->>Browser: Render HTML danh sách kèm Alert xanh "Cập nhật danh mục mỹ phẩm thành công!"
                    deactivate View
                    Browser-->>Admin: Hiển thị trang danh sách cập nhật thông tin mới
                end
            end
        end
    end
```

---

## 3. Class Diagram

Mô hình cấu trúc lớp chi tiết giữa Entity `Category`, `CategoryUpdateDTO` và `CategoryResponseDTO` tham gia trong use case `admin-update-category`.

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

    class CategoryUpdateDTO {
        <<DTO>>
        -String id
        -String categoryCode
        -String name
        -String description
        -Boolean active
        +getId() String
        +setId(String id) void
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

    Category ..> CategoryUpdateDTO : mapped to DTO for form display (categoryCode read-only)
    CategoryUpdateDTO ..> Category : state applied to entity in Service Layer
    Category ..> CategoryResponseDTO : mapped to ResponseDTO via Builder after save
```
