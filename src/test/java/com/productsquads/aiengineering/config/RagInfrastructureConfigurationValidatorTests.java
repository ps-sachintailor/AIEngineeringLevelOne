package com.productsquads.aiengineering.config;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

class RagInfrastructureConfigurationValidatorTests {

    private final Environment environment = mock(Environment.class);

    @Test
    void acceptsOllamaEmbeddingsWithoutCredentials() {
        RagInfrastructureProperties properties = properties(
                "ollama", URI.create("http://localhost:11434"));

        assertThatCode(() -> new RagInfrastructureConfigurationValidator(properties, environment))
                .doesNotThrowAnyException();
    }

    @Test
    void requiresOpenAiCompatibleEmbeddingApiKeyWithoutExposingAValue() {
        RagInfrastructureProperties properties = properties(
                "openai-compatible", URI.create("https://models.example.test/v1"));

        assertThatThrownBy(() -> new RagInfrastructureConfigurationValidator(properties, environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("OpenAI-compatible embedding API key is required");
    }

    @Test
    void acceptsCompleteOpenAiCompatibleEmbeddingConfiguration() {
        when(environment.getProperty("spring.ai.openai.embedding.api-key"))
                .thenReturn("test-only-embedding-key");
        RagInfrastructureProperties properties = properties(
                "openai-compatible", URI.create("https://models.example.test/v1"));

        assertThatCode(() -> new RagInfrastructureConfigurationValidator(properties, environment))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsNonHttpEmbeddingBaseUrl() {
        RagInfrastructureProperties properties = properties(
                "ollama", URI.create("file:///tmp/embeddings"));

        assertThatThrownBy(() -> new RagInfrastructureConfigurationValidator(properties, environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Embedding provider base URL must be a valid HTTP(S) URL");
    }

    @Test
    void rejectsUnsupportedEmbeddingProvider() {
        RagInfrastructureProperties properties = properties(
                "unsupported", URI.create("https://models.example.test/v1"));

        assertThatThrownBy(() -> new RagInfrastructureConfigurationValidator(properties, environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unsupported embedding provider. Use ollama or openai-compatible");
    }

    private static RagInfrastructureProperties properties(String provider, URI baseUrl) {
        return new RagInfrastructureProperties(
                new RagInfrastructureProperties.Embedding(
                        provider,
                        "test-embedding-model",
                        baseUrl));
    }
}
