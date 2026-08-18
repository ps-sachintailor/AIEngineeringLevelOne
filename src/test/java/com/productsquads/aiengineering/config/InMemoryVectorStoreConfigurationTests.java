package com.productsquads.aiengineering.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;

class InMemoryVectorStoreConfigurationTests {

    private final InMemoryVectorStoreConfiguration configuration =
            new InMemoryVectorStoreConfiguration();

    @Test
    void storesDocumentsAndReturnsTheMostSimilarDocumentInMemory() {
        VectorStore vectorStore = configuration.vectorStore(new DeterministicEmbeddingModel());
        vectorStore.add(List.of(
                new Document("Spring AI supports retrieval-augmented generation."),
                new Document("Relational databases store structured records.")));

        List<Document> results = vectorStore.similaritySearch(SearchRequest.builder()
                .query("Spring framework AI")
                .topK(1)
                .build());

        assertThat(vectorStore).isInstanceOf(SimpleVectorStore.class);
        assertThat(results)
                .singleElement()
                .extracting(Document::getText)
                .isEqualTo("Spring AI supports retrieval-augmented generation.");
    }

    @Test
    void aNewStoreDoesNotRetainDocumentsFromAnotherInstance() {
        EmbeddingModel embeddingModel = new DeterministicEmbeddingModel();
        VectorStore firstStore = configuration.vectorStore(embeddingModel);
        firstStore.add(List.of(new Document("Spring AI data exists only in this process.")));

        VectorStore newStore = configuration.vectorStore(embeddingModel);

        assertThat(newStore.similaritySearch(SearchRequest.builder()
                .query("Spring AI")
                .topK(1)
                .build())).isEmpty();
    }

    private static final class DeterministicEmbeddingModel implements EmbeddingModel {

        @Override
        public EmbeddingResponse call(EmbeddingRequest request) {
            List<Embedding> embeddings = new ArrayList<>();
            for (int index = 0; index < request.getInstructions().size(); index++) {
                embeddings.add(new Embedding(vectorFor(request.getInstructions().get(index)), index));
            }
            return new EmbeddingResponse(embeddings);
        }

        @Override
        public float[] embed(Document document) {
            return vectorFor(document.getText());
        }

        private static float[] vectorFor(String content) {
            String normalized = content.toLowerCase(Locale.ROOT);
            return normalized.contains("spring") || normalized.contains("rag")
                    ? new float[] {1.0f, 0.0f}
                    : new float[] {0.0f, 1.0f};
        }
    }
}
