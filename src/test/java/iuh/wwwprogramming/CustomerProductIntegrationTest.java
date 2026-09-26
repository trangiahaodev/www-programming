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
    @DisplayName("Nhiệm vụ 3: Tải dữ liệu sản phẩm trang chủ (Hot, New, Thương hiệu)")
    void testHomePageProductServices() {
        var hotProducts = productService.getHotProducts(6);
        assertThat(hotProducts).isNotEmpty();
        assertThat(hotProducts.size()).isLessThanOrEqualTo(6);

        var newProducts = productService.getNewProducts(6);
        assertThat(newProducts).isNotEmpty();
        assertThat(newProducts.size()).isLessThanOrEqualTo(6);

        var topBrands = productService.getTopBrands();
        assertThat(topBrands).isNotEmpty();

        var featuredBrands = productService.getFeaturedBrandProducts(4);
        assertThat(featuredBrands).isNotEmpty();
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
