package iuh.wwwprogramming.config;

import org.springframework.ai.chat.client.ChatClient;
import com.google.genai.Client;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.model.openai.autoconfigure.OpenAiChatAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Creates provider-specific clients only when the corresponding starter has a usable model. */
@Configuration
@AutoConfigureAfter(OpenAiChatAutoConfiguration.class)
public class AiChatClientConfig {

    @Bean("geminiChatClient")
    @ConditionalOnExpression("'${app.ai.gemini.api-key:}' != '' and '${app.ai.gemini.api-key:}' != '__disabled__'")
    ChatClient geminiChatClient(
            @Value("${app.ai.gemini.api-key}") String apiKey,
            @Value("${app.ai.gemini.model:gemini-3.5-flash}") String modelName) {
        var model = GoogleGenAiChatModel.builder()
                .genAiClient(Client.builder().apiKey(apiKey).build())
                .options(GoogleGenAiChatOptions.builder()
                        .model(GoogleGenAiChatModel.ChatModel.valueOf(toEnumName(modelName)))
                        .build())
                .build();
        return ChatClient.create(model);
    }

    private static String toEnumName(String modelName) {
        return modelName.toUpperCase().replace('-', '_').replace('.', '_');
    }

    @Bean("openAiChatClient")
    @ConditionalOnExpression("'${app.ai.openai.api-key:}' != '' and '${app.ai.openai.api-key:}' != '__disabled__'")
    ChatClient openAiChatClient(OpenAiChatModel model) {
        return ChatClient.create(model);
    }
}
