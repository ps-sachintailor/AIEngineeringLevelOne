package com.productsquads.aiengineering.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.ai")
public record AiModelProperties(
        @NotBlank String provider,
        @NotBlank String model) {
}
