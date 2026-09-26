package iuh.wwwprogramming.controller.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NewsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Tin tức 1: Truy cập trang danh sách tin tức & cẩm nang thành công")
    void testNewsListPage_Success() throws Exception {
        mockMvc.perform(get("/tin-tuc"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/news-list"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("featuredArticle"))
                .andExpect(model().attribute("activeMenu", "news"))
                .andExpect(model().attribute("articles", hasSize(greaterThanOrEqualTo(5))));

        mockMvc.perform(get("/cam-nang"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/news-list"))
                .andExpect(model().attributeExists("articles"));
    }

    @Test
    @DisplayName("Tin tức 2: Lọc danh sách bài viết theo chuyên mục thành công")
    void testNewsListFilterByCategory_Success() throws Exception {
        mockMvc.perform(get("/tin-tuc").param("category", "Chăm sóc da"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/news-list"))
                .andExpect(model().attribute("selectedCategory", "Chăm sóc da"))
                .andExpect(model().attribute("articles", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Tin tức 3: Xem chi tiết bài viết hợp lệ kèm sản phẩm gợi ý và bài viết liên quan")
    void testNewsDetailPage_Success() throws Exception {
        mockMvc.perform(get("/tin-tuc/xu-huong-lam-dep-2026-thien-nhien-va-cham-soc-da"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/news-detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("relatedNews"))
                .andExpect(model().attribute("activeMenu", "news"))
                .andExpect(model().attribute("article", hasProperty("linkedProducts", notNullValue())));
    }

    @Test
    @DisplayName("Tin tức 4: Truy cập bài viết không tồn tại sẽ chuyển hướng an toàn kèm flash error message")
    void testNewsDetailNotFound_Redirect() throws Exception {
        mockMvc.perform(get("/tin-tuc/bai-viet-khong-ton-tai-xyz"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tin-tuc"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("Tin tức 5: Đăng ký nhận bản tin cẩm nang Skincare với email hợp lệ")
    void testNewsletterSubscription_Success() throws Exception {
        mockMvc.perform(post("/tin-tuc/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"khachhang@gmail.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("Tin tức 6: Đăng ký bản tin với email không hợp lệ sẽ trả về lỗi HTTP 400")
    void testNewsletterSubscription_InvalidEmail() throws Exception {
        mockMvc.perform(post("/tin-tuc/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalid-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
