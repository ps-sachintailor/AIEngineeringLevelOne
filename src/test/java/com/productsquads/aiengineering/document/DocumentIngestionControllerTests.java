package com.productsquads.aiengineering.document;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.productsquads.aiengineering.web.GlobalExceptionHandler;
import jakarta.validation.Validation;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

class DocumentIngestionControllerTests {

    private DocumentIngestionService documentIngestionService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        documentIngestionService = mock(DocumentIngestionService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new DocumentIngestionController(documentIngestionService))
                .setValidator(new SpringValidatorAdapter(
                        Validation.buildDefaultValidatorFactory().getValidator()))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void validDocumentReturnsCreatedResponse() throws Exception {
        DocumentIngestionRequest request = new DocumentIngestionRequest(
                "Spring AI document content.", Map.<String, Object>of("source", "test"));
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
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
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
