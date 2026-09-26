# Task Plan: Quản trị viên thêm mới sản phẩm (admin-create-product)

## Mục tiêu
Cung cấp màn hình và chức năng cho Quản trị viên (Admin) tạo mới sản phẩm mỹ phẩm trong hệ thống với các thông tin: Mã sản phẩm (tự động sinh hoặc nhập chuẩn Business Key), Tên sản phẩm, Danh mục mỹ phẩm (`Category`), Thương hiệu (`brand`), Giá bán (`price`), Số lượng tồn kho (`stockQuantity`), Mô tả (`description`), Hình ảnh (`imageUrl`), và Trạng thái hoạt động (`active`). Áp dụng Dual-Key Strategy, validation dữ liệu 2 lớp (JSR-380 + Service unique check), quan hệ `@ManyToOne` với `Category`, và PRG pattern với Flash message.

## Tiến độ 6 bước ADD (Agent-Driven Development)
- [x] Phase 1: `/create-usecase` (Đặc tả nghiệp vụ)
- [x] Phase 2: `/scaffold` (Sinh code Backend JPA/Controller)
- [x] Phase 3: `/mermaid` (Vẽ sơ đồ)
- [x] Phase 4: `/ux` (Lập bản vẽ UX)
- [x] Phase 5: `/client` (Dựng giao diện Thymeleaf)
- [x] Phase 6: `/supervisor audit-feature` (Thẩm định mã nguồn)

## Quyết định kiến trúc & Đặc tả (Decisions)
- Mã use case: `uc002a-admin-create-product` (thuộc nhóm quản lý sản phẩm `uc002`).
- Dual-Key Strategy: Surrogate Key (`id` UUID 36 chars) + Business Key (`productCode` ví dụ: `SP26000001` duy nhất, bất biến).
- Quan hệ: `Product` `@ManyToOne(fetch = FetchType.LAZY)` `@JoinColumn(name = "category_id")` liên kết với `Category`.
- Validation 2 lớp: JSR-380 (`ProductCreateDTO`) + Service logic check trùng mã/tên và kiểm tra danh mục hợp lệ.
- Endpoint: `GET /admin/products/create` (hiển thị form kèm danh sách danh mục hoạt động) và `POST /admin/products/create` (xử lý tạo và redirect về `/admin/products` kèm Flash message).
- Giao diện Thymeleaf: Kế thừa Base Layout `layout/admin-layout.html`, tuân thủ phong cách Minimalism & Subtle Neo-brutalism (8px radius, zero shadow, màu nhấn hồng `#ec4899`, preview ảnh, live char counter, selector danh mục).

## Lỗi & Khắc phục (Errors - 3-Strike Rule)
- **Strike Count: 0/3**
