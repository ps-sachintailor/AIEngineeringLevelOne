package com.productsquads.aiengineering;

import static org.assertj.core.api.Assertions.assertThat;

import com.productsquads.aiengineering.config.AiModelProperties;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "OPENAI_BASE_URL=https://models.example.test/v1",
        "OPENAI_API_KEY=test-only-key",
        "OPENAI_CHAT_MODEL=test-chat-model"
})
@ActiveProfiles({"openai-compatible", "local-rag"})
class OpenAiCompatibleProfileTests {

    @Autowired
    private AiModelProperties properties;

    @Autowired
    private ChatModel chatModel;

    @Test
    void openAiCompatibleProfileCreatesConfiguredChatModelWithoutCallingProvider() {
        assertThat(properties.provider()).isEqualTo("openai-compatible");
        assertThat(properties.model()).isEqualTo("test-chat-model");
        assertThat(properties.baseUrl()).isEqualTo(URI.create("https://models.example.test/v1"));
        assertThat(chatModel.getClass().getSimpleName()).contains("OpenAi");
    }
}
