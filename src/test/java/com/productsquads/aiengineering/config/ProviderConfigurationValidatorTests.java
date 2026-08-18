package com.productsquads.aiengineering.config;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

class ProviderConfigurationValidatorTests {

    private final Environment environment = mock(Environment.class);

    @Test
    void acceptsOllamaWithoutCloudCredentials() {
        AiModelProperties properties = new AiModelProperties(
                "ollama", "llama3.2:3b", URI.create("http://localhost:11434"));

        assertThatCode(() -> new ProviderConfigurationValidator(properties, environment))
                .doesNotThrowAnyException();
    }

    @Test
    void requiresOpenAiCompatibleApiKeyWithoutExposingAValue() {
        AiModelProperties properties = new AiModelProperties(
                "openai-compatible", "test-model", URI.create("https://models.example.test/v1"));

        assertThatThrownBy(() -> new ProviderConfigurationValidator(properties, environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("OpenAI-compatible API key is required");
    }

    @Test
    void rejectsInvalidProviderBaseUrl() {
        AiModelProperties properties = new AiModelProperties(
                "ollama", "test-model", URI.create("file:///tmp/model"));

        assertThatThrownBy(() -> new ProviderConfigurationValidator(properties, environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("AI provider base URL must be a valid HTTP(S) URL");
    }

    @Test
    void acceptsCompleteOpenAiCompatibleConfiguration() {
        when(environment.getProperty("spring.ai.openai.api-key")).thenReturn("test-only-key");
        AiModelProperties properties = new AiModelProperties(
                "openai-compatible", "test-model", URI.create("https://models.example.test/v1"));

        assertThatCode(() -> new ProviderConfigurationValidator(properties, environment))
                .doesNotThrowAnyException();
    }
}
