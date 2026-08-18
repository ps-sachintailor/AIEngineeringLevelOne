package com.productsquads.aiengineering;

import static org.assertj.core.api.Assertions.assertThat;

import com.productsquads.aiengineering.config.RagInfrastructureProperties;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"ollama", "local-rag"})
class LocalRagProfileTests {

    @Autowired
    private RagInfrastructureProperties properties;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private VectorStore vectorStore;

    @Test
    void createsLocalOllamaEmbeddingAndInMemoryVectorStore() {
        assertThat(properties.embedding().provider()).isEqualTo("ollama");
        assertThat(properties.embedding().model()).isEqualTo("nomic-embed-text");
        assertThat(embeddingModel.getClass().getSimpleName()).contains("Ollama");
        assertThat(vectorStore).isInstanceOf(SimpleVectorStore.class);
    }
}
