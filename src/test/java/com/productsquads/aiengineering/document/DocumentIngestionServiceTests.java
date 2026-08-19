package com.productsquads.aiengineering.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SimpleVectorStore;

class DocumentIngestionServiceTests {

    private final SimpleVectorStore vectorStore = mock(SimpleVectorStore.class);
    private final DocumentIngestionService service = new DocumentIngestionService(vectorStore);

    @Test
    void embedsAndStoresDocumentWithMetadata() {
        DocumentIngestionResponse response = service.ingest(new DocumentIngestionRequest(
                "Spring AI stores embeddings in a vector store.",
                Map.of("source", "documentation")));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Document>> documents = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).add(documents.capture());

        assertThat(documents.getValue())
                .singleElement()
                .satisfies(document -> {
                    assertThat(document.getText())
                            .isEqualTo("Spring AI stores embeddings in a vector store.");
                    assertThat(document.getMetadata())
                            .containsEntry("source", "documentation");
                    assertThat(response.documentId()).isEqualTo(document.getId());
                });
        assertThat(response.status()).isEqualTo("embedded");
    }

    @Test
    void usesEmptyMetadataWhenItIsOmitted() {
        DocumentIngestionResponse response = service.ingest(
                new DocumentIngestionRequest("Content without metadata.", null));

        assertThat(response.documentId()).isNotBlank();
        assertThat(response.status()).isEqualTo("embedded");
    }

    @Test
    void wrapsEmbeddingProviderFailures() {
        doThrow(new IllegalStateException("provider secret must not be exposed"))
                .when(vectorStore).add(anyList());

        assertThatThrownBy(() -> service.ingest(
                new DocumentIngestionRequest("Content", Map.of())))
                .isInstanceOf(DocumentIngestionException.class)
                .hasMessage("Document embedding failed")
                .hasCauseInstanceOf(IllegalStateException.class);
    }
}
