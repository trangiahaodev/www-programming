# Task Plan: Quản trị viên tìm kiếm & lọc nâng cao sản phẩm mỹ phẩm (admin-search-products)

## Mục tiêu
Cung cấp và mở rộng tính năng Tìm kiếm & Bộ lọc nâng cao sản phẩm mỹ phẩm cho Quản trị viên (Admin). Cho phép lọc đa tiêu chí: Từ khóa (Tên/Mã sản phẩm), Danh mục mỹ phẩm (`categoryId`), Khoảng giá (`minPrice` - `maxPrice`), Trạng thái tồn kho (`stockStatus`: Tất cả, Còn hàng, Hết hàng, Sắp hết hàng < 10), Trạng thái kích hoạt (`active`), Sắp xếp theo tiêu chí (`sortBy`: Mới nhất, Giá tăng dần, Giá giảm dần, Tên A-Z, Tồn kho tăng/giảm dần) kèm phân trang mượt mà trên chuẩn Thymeleaf SSR và Spring Data JPA.

## Tiến độ 6 bước ADD (Agent-Driven Development)
- [x] Phase 1: `/create-usecase` (Đặc tả nghiệp vụ)
- [x] Phase 2: `/scaffold` (Sinh code Backend JPA/Controller)
- [x] Phase 3: `/mermaid` (Vẽ sơ đồ)
- [x] Phase 4: `/ux` (Lập bản vẽ UX)
- [x] Phase 5: `/client` (Dựng giao diện Thymeleaf)
- [x] Phase 6: `/supervisor audit-feature` (Thẩm định mã nguồn)

## Quyết định kiến trúc & Thiết kế (Decisions)
- Mã use case: `uc002e-admin-search-products` (thuộc nhóm `uc002`, `e` = Search/Filter extension).
- URL Endpoint: `GET /admin/products` (mở rộng nhận thêm DTO/query params: `keyword`, `categoryId`, `minPrice`, `maxPrice`, `stockStatus`, `active`, `sortBy`, `page`, `size`).
- Tầng JPA/Repository:
  - Viết JPQL truy vấn động hoặc `@Query` đa điều kiện tối ưu `JOIN FETCH p.category` kèm `countQuery`.
  - Hỗ trợ sắp xếp linh hoạt qua `Sort` / `Pageable`.
- Tầng Service:
  - `searchProductsAdvanced(ProductSearchCriteriaDTO criteria, Pageable pageable)` trả về `Page<ProductResponseDTO>`.
  - Transaction `@Transactional(readOnly = true)`.
- Giao diện người dùng:
  - Nâng cấp Toolbar tìm kiếm trên [`product-list.html`](file:///E:/java-www/www-programming/src/main/resources/templates/admin/product-list.html) thành bộ lọc thông minh đa tiêu chí phong cách Minimalism & Subtle Neo-Brutalism (Filter Drawer / Collapsible Advanced Filter hoặc Filter Bar tiện lợi, reset filter 1 click).

## Lỗi & Khắc phục (Errors - 3-Strike Rule)
- **Strike Count: 0/3**
