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
    void acceptsLocalChromaAndOllamaEmbeddingsWithoutCredentials() {
        RagInfrastructureProperties properties = properties(
                "local",
                URI.create("http://localhost"),
                "ollama",
                URI.create("http://localhost:11434"));

        assertThatCode(() -> new RagInfrastructureConfigurationValidator(properties, environment))
                .doesNotThrowAnyException();
    }

    @Test
    void requiresChromaCloudCredentialsWithoutExposingAValue() {
        RagInfrastructureProperties properties = properties(
                "cloud",
                URI.create("https://api.trychroma.com"),
                "openai-compatible",
                URI.create("https://models.example.test/v1"));

        assertThatThrownBy(() -> new RagInfrastructureConfigurationValidator(properties, environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Chroma Cloud key token is required");
    }

    @Test
    void requiresOpenAiCompatibleEmbeddingApiKey() {
        configureChromaCloud();
        RagInfrastructureProperties properties = properties(
                "cloud",
                URI.create("https://api.trychroma.com"),
                "openai-compatible",
                URI.create("https://models.example.test/v1"));

        assertThatThrownBy(() -> new RagInfrastructureConfigurationValidator(properties, environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("OpenAI-compatible embedding API key is required");
    }

    @Test
    void acceptsCompleteRemoteConfiguration() {
        configureChromaCloud();
        when(environment.getProperty("spring.ai.openai.embedding.api-key"))
                .thenReturn("test-only-embedding-key");
        RagInfrastructureProperties properties = properties(
                "cloud",
                URI.create("https://api.trychroma.com"),
                "openai-compatible",
                URI.create("https://models.example.test/v1"));

        assertThatCode(() -> new RagInfrastructureConfigurationValidator(properties, environment))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsNonHttpChromaHost() {
        RagInfrastructureProperties properties = properties(
                "local",
                URI.create("file:///tmp/chroma"),
                "ollama",
                URI.create("http://localhost:11434"));

        assertThatThrownBy(() -> new RagInfrastructureConfigurationValidator(properties, environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Chroma host must be a valid HTTP(S) URL");
    }

    private void configureChromaCloud() {
        when(environment.getProperty("spring.ai.vectorstore.chroma.client.key-token"))
                .thenReturn("test-only-chroma-key");
        when(environment.getProperty("spring.ai.vectorstore.chroma.tenant-name"))
                .thenReturn("test-tenant");
        when(environment.getProperty("spring.ai.vectorstore.chroma.database-name"))
                .thenReturn("test-database");
    }

    private static RagInfrastructureProperties properties(
            String chromaMode,
            URI chromaHost,
            String embeddingProvider,
            URI embeddingBaseUrl) {
        return new RagInfrastructureProperties(
                new RagInfrastructureProperties.Chroma(
                        chromaMode,
                        chromaHost,
                        8000,
                        "test-collection",
                        false,
                        false),
                new RagInfrastructureProperties.Embedding(
                        embeddingProvider,
                        "test-embedding-model",
                        embeddingBaseUrl));
    }
}
