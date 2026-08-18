package com.productsquads.aiengineering.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.rag")
public record RagInfrastructureProperties(
        @Valid @NotNull Embedding embedding) {

    public record Embedding(
            @NotBlank String provider,
            @NotBlank String model,
            @NotNull URI baseUrl) {
    }
}
