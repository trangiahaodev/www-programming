package iuh.wwwprogramming;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.seed-products=false")
class TrackingAiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void anonymousTrackingAdviceReturnsMockResponseWithoutApiKeys() throws Exception {
        mockMvc.perform(get("/api/tracking/ai-advice"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.advice").isString());
    }

    @Test
    void anonymousTrackingQuickAdviceReturnsMockResponseWithoutApiKeys() throws Exception {
        mockMvc.perform(get("/api/tracking/ai-advice-quick")
                        .param("clickCount", "3")
                        .param("dwellTimeSeconds", "15")
                        .param("lastProductName", "Kem Chống Nắng"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.advice").isString());
    }
}
