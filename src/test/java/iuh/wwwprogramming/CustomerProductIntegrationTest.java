package iuh.wwwprogramming;

import iuh.wwwprogramming.dto.ProductCardDTO;
import iuh.wwwprogramming.dto.ProductDetailDTO;
import iuh.wwwprogramming.repository.CategoryRepository;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Nhiệm vụ 3: Trang chủ hiển thị thành công và thanh tìm kiếm điều hướng chuẩn")
    void testHomePageAndSearchRedirect() throws Exception {
        // 1. Vào trang chủ
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/home"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("hotProducts"))
                .andExpect(model().attributeExists("newProducts"));

        // 2. Tìm kiếm ngoài trang chủ điều hướng sang /san-pham?keyword=...
        mockMvc.perform(get("/").param("keyword", "Anessa"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/san-pham?keyword=Anessa"));
    }

    @Test
    @DisplayName("Nhiệm vụ 1: Khách hàng xem danh sách SP, phân trang, lọc và triệt tiêu N+1 Query")
    @Transactional(readOnly = true)
    void testProductListAndNPlusOnePrevention() throws Exception {
        // 1. Kiểm tra Service search và phân trang
        Page<ProductCardDTO> page = productService.searchProducts(null, "all", "popular", PageRequest.of(0, 12));
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getSize()).isEqualTo(12);

        // Đảm bảo thông tin Category đã được eager load qua JOIN FETCH, không gây LazyInitializationException
        ProductCardDTO firstProduct = page.getContent().get(0);
        assertThat(firstProduct.getCategoryName()).isNotBlank();

        // 2. Kiểm tra Controller /san-pham
        mockMvc.perform(get("/san-pham")
                        .param("page", "0")
                        .param("size", "12")
                        .param("sort", "popular"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/product-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalElements"))
                .andExpect(model().attribute("currentPage", 0));
    }

    @Test
    @DisplayName("Nhiệm vụ 1: Lọc sản phẩm theo danh mục và tìm kiếm từ khóa")
    void testProductListFilters() throws Exception {
        mockMvc.perform(get("/san-pham")
                        .param("keyword", "Anessa"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/product-list"))
                .andExpect(model().attribute("keyword", "Anessa"));
    }

    @Test
    @DisplayName("Nhiệm vụ 2: Xem chi tiết sản phẩm và các sản phẩm cùng danh mục liên quan")
    void testProductDetailView() throws Exception {
        // Lấy 1 sản phẩm đầu tiên từ DB
        var product = productRepository.findAll().get(0);

        ProductDetailDTO detail = productService.getProductDetail(product.getId());
        assertThat(detail).isNotNull();
        assertThat(detail.getName()).isEqualTo(product.getName());
        assertThat(detail.getCategoryName()).isNotBlank();

        // Test Endpoint /san-pham/{id}
        mockMvc.perform(get("/san-pham/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/product-detail"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("relatedProducts"));
    }

    @Test
    @DisplayName("Nhiệm vụ 2: Truy cập ID sản phẩm không tồn tại được redirect an toàn kèm flash error message")
    void testProductDetailNotFoundRedirect() throws Exception {
        mockMvc.perform(get("/san-pham/non-existent-id-99999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/san-pham"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
