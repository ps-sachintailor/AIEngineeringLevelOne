package com.productsquads.aiengineering.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record DocumentIngestionRequest(
        @NotBlank(message = "content is required")
        @Size(max = 20_000, message = "content must not exceed 20000 characters")
        String content,
        @Size(max = 50, message = "metadata must not contain more than 50 entries")
        Map<String, Object> metadata) {
}
