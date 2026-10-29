package iuh.wwwprogramming.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@Slf4j
public class AiAdvisorService {

    private final Optional<ChatClient> geminiChatClient;
    private final Optional<ChatClient> openAiChatClient;
    private final boolean enabled;
    private final String mockResponse;
    private final String geminiApiKey;
    private final String openAiApiKey;

    public AiAdvisorService(
            @Qualifier("geminiChatClient") Optional<ChatClient> geminiChatClient,
            @Qualifier("openAiChatClient") Optional<ChatClient> openAiChatClient,
            @Value("${app.ai.enabled:true}") boolean enabled,
            @Value("${app.ai.mock-response}") String mockResponse,
            @Value("${app.ai.gemini.api-key:}") String geminiApiKey,
            @Value("${app.ai.openai.api-key:}") String openAiApiKey) {
        this.geminiChatClient = geminiChatClient;
        this.openAiChatClient = openAiChatClient;
        this.enabled = enabled;
        this.mockResponse = mockResponse;
        this.geminiApiKey = geminiApiKey;
        this.openAiApiKey = openAiApiKey;
        log.info("AI config: enabled={}, geminiKeyPresent={}, geminiClientPresent={}, openAiKeyPresent={}, openAiClientPresent={}",
                enabled,
                StringUtils.hasText(geminiApiKey) && !"__disabled__".equals(geminiApiKey),
                geminiChatClient.isPresent(),
                StringUtils.hasText(openAiApiKey) && !"__disabled__".equals(openAiApiKey),
                openAiChatClient.isPresent());
    }

    public String advise(TrackingContext context) {
        if (!enabled) {
            return mockResponse;
        }

        String prompt = buildPrompt(context);
        String advice = callProvider("Gemini", geminiApiKey, geminiChatClient, prompt);
        if (StringUtils.hasText(advice)) {
            return advice;
        }

        advice = callProvider("OpenAI", openAiApiKey, openAiChatClient, prompt);
        return StringUtils.hasText(advice) ? advice : mockResponse;
    }

    private String callProvider(String provider, String apiKey, Optional<ChatClient> client, String prompt) {
        if (!StringUtils.hasText(apiKey) || "__disabled__".equals(apiKey) || client.isEmpty()) {
            return null;
        }
        try {
            return client.get().prompt().user(prompt).call().content();
        } catch (Exception ex) {
            log.warn("{} AI provider unavailable ({}); using fallback", provider,
                    ex.getClass().getSimpleName());
            return null;
        }
    }

    private String buildPrompt(TrackingContext context) {
        return "Bạn là trợ lý tư vấn mỹ phẩm PinkyCloud. Hãy trả lời ngắn gọn bằng tiếng Việt, "
                + "thân thiện và không bịa thông tin y tế. Dữ liệu phiên xem hàng: "
                + "số lượt click=" + context.clickCount() + ", thời gian xem=" + context.dwellTimeSeconds()
                + " giây, sản phẩm gần đây=" + context.lastProductName()
                + ". Hãy đưa ra một gợi ý mua sắm hữu ích.";
    }

    public record TrackingContext(int clickCount, int dwellTimeSeconds, String lastProductName) {
        public TrackingContext {
            lastProductName = StringUtils.hasText(lastProductName) ? lastProductName : "chưa xác định";
        }
    }
}
