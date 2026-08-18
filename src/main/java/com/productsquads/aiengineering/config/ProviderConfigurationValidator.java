package com.productsquads.aiengineering.config;

import java.net.URI;
import java.util.Set;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ProviderConfigurationValidator {

    private static final String OPENAI_COMPATIBLE = "openai-compatible";
    private static final Set<String> SUPPORTED_PROVIDERS = Set.of("ollama", OPENAI_COMPATIBLE);

    public ProviderConfigurationValidator(
            AiModelProperties properties,
            Environment environment) {
        validateProvider(properties.provider());
        validateHttpUrl(properties.baseUrl());

        if (OPENAI_COMPATIBLE.equals(properties.provider())) {
            requireConfigured(
                    environment.getProperty("spring.ai.openai.api-key"),
                    "OpenAI-compatible API key is required");
        }
    }

    private static void validateProvider(String provider) {
        if (!SUPPORTED_PROVIDERS.contains(provider)) {
            throw new IllegalStateException(
                    "Unsupported AI provider. Use the ollama or openai-compatible profile");
        }
    }

    private static void validateHttpUrl(URI baseUrl) {
        String scheme = baseUrl.getScheme();
        if (!("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                || !StringUtils.hasText(baseUrl.getHost())) {
            throw new IllegalStateException("AI provider base URL must be a valid HTTP(S) URL");
        }
    }

    private static void requireConfigured(String value, String errorMessage) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalStateException(errorMessage);
        }
    }
}
