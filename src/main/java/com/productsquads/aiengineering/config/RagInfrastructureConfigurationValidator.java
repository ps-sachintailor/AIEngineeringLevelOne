package com.productsquads.aiengineering.config;

import java.net.URI;
import java.util.Set;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RagInfrastructureConfigurationValidator {

    private static final String OPENAI_COMPATIBLE = "openai-compatible";
    private static final Set<String> SUPPORTED_EMBEDDING_PROVIDERS =
            Set.of("ollama", OPENAI_COMPATIBLE);

    public RagInfrastructureConfigurationValidator(
            RagInfrastructureProperties properties,
            Environment environment) {
        validateEmbedding(properties.embedding(), environment);
    }

    private static void validateEmbedding(
            RagInfrastructureProperties.Embedding embedding,
            Environment environment) {
        if (!SUPPORTED_EMBEDDING_PROVIDERS.contains(embedding.provider())) {
            throw new IllegalStateException(
                    "Unsupported embedding provider. Use ollama or openai-compatible");
        }
        validateHttpUrl(
                embedding.baseUrl(),
                "Embedding provider base URL must be a valid HTTP(S) URL");

        if (OPENAI_COMPATIBLE.equals(embedding.provider())) {
            requireConfigured(
                    environment.getProperty("spring.ai.openai.embedding.api-key"),
                    "OpenAI-compatible embedding API key is required");
        }
    }

    private static void validateHttpUrl(URI uri, String errorMessage) {
        String scheme = uri.getScheme();
        if (!("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                || !StringUtils.hasText(uri.getHost())) {
            throw new IllegalStateException(errorMessage);
        }
    }

    private static void requireConfigured(String value, String errorMessage) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalStateException(errorMessage);
        }
    }
}
