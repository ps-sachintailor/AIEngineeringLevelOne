package com.productsquads.aiengineering.document;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.productsquads.aiengineering.web.GlobalExceptionHandler;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DocumentIngestionControllerTests {

    private DocumentIngestionService documentIngestionService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        documentIngestionService = mock(DocumentIngestionService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new DocumentIngestionController(documentIngestionService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void validDocumentReturnsCreatedResponse() throws Exception {
        DocumentIngestionRequest request = new DocumentIngestionRequest(
                "Spring AI document content.", Map.of("source", "test"));
        when(documentIngestionService.ingest(request))
                .thenReturn(new DocumentIngestionResponse("document-123", "embedded"));

        mockMvc.perform(post("/api/v1/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "Spring AI document content.",
                                  "metadata": {"source": "test"}
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.documentId").value("document-123"))
                .andExpect(jsonPath("$.status").value("embedded"));
    }

    @Test
    void metadataIsOptional() throws Exception {
        DocumentIngestionRequest request = new DocumentIngestionRequest("Content", null);
        when(documentIngestionService.ingest(request))
                .thenReturn(new DocumentIngestionResponse("document-456", "embedded"));

        mockMvc.perform(post("/api/v1/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Content\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documentId").value("document-456"));
    }

    @Test
    void blankContentReturnsValidationError() throws Exception {
        mockMvc.perform(post("/api/v1/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors.content").value("content is required"));
    }

    @Test
    void embeddingFailureReturnsControlledBadGateway() throws Exception {
        DocumentIngestionRequest request = new DocumentIngestionRequest("Content", null);
        when(documentIngestionService.ingest(request))
                .thenThrow(new DocumentIngestionException(
                        new IllegalStateException("api-key=must-not-be-returned")));

        mockMvc.perform(post("/api/v1/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Content\"}"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value(
                        "The embedding service is temporarily unavailable. Please try again later."));
    }
}
