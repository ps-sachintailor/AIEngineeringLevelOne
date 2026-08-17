package com.productsquads.aiengineering.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void malformedJsonReturnsBadRequestWithActionableMessage() {
        HttpServletRequest request = requestFor("/api/v1/chat");

        ResponseEntity<ApiError> response = handler.handleUnreadableMessage(
                mock(HttpMessageNotReadableException.class), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).contains("Expected").contains("message");
    }

    @Test
    void ollamaConnectionFailureReturnsBadGateway() {
        HttpServletRequest request = requestFor("/api/v1/chat");

        ResponseEntity<ApiError> response = handler.handleModelConnection(
                new RestClientException("connection refused"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).contains("Ollama");
    }

    @Test
    void unsupportedMethodReturnsMethodNotAllowed() {
        HttpServletRequest request = requestFor("/api/v1/chat");

        ResponseEntity<ApiError> response = handler.handleUnsupportedMethod(
                new HttpRequestMethodNotSupportedException("DELETE"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    private static HttpServletRequest requestFor(String path) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(path);
        return request;
    }
}
