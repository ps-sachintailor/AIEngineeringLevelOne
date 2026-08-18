package com.productsquads.aiengineering.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.rag")
public record RagInfrastructureProperties(
        @Valid @NotNull Chroma chroma,
        @Valid @NotNull Embedding embedding) {

    public record Chroma(
            @NotBlank String mode,
            @NotNull URI host,
            @Min(1) @Max(65535) int port,
            @NotBlank String collectionName,
            boolean initializeSchema,
            boolean checkOnStartup) {
    }

    public record Embedding(
            @NotBlank String provider,
            @NotBlank String model,
            @NotNull URI baseUrl) {
    }
}
