package com.productsquads.aiengineering.ask;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AskRequest(
        @NotBlank(message = "question is required")
        @Size(max = 4_000, message = "question must not exceed 4000 characters")
        String question) {
}
