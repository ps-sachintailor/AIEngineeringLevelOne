package com.productsquads.aiengineering.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank(message = "message is required")
        @Size(max = 4_000, message = "message must not exceed 4000 characters")
        String message) {
}
