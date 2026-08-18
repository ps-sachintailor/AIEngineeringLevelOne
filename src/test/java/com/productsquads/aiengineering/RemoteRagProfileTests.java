package com.productsquads.aiengineering;

import static org.assertj.core.api.Assertions.assertThat;

import com.productsquads.aiengineering.config.RagInfrastructureProperties;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "EMBEDDING_OPENAI_BASE_URL=https://models.example.test/v1",
        "EMBEDDING_OPENAI_API_KEY=test-only-embedding-key",
        "EMBEDDING_OPENAI_MODEL=test-embedding-model"
})
@ActiveProfiles({"ollama", "remote-rag"})
class RemoteRagProfileTests {

    @Autowired
    private RagInfrastructureProperties properties;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private VectorStore vectorStore;

    @Test
    void createsRemoteOpenAiCompatibleEmbeddingAndInMemoryVectorStore() {
        assertThat(properties.embedding().provider()).isEqualTo("openai-compatible");
        assertThat(properties.embedding().model()).isEqualTo("test-embedding-model");
        assertThat(properties.embedding().baseUrl())
                .isEqualTo(URI.create("https://models.example.test/v1"));
        assertThat(embeddingModel.getClass().getSimpleName()).contains("OpenAi");
        assertThat(vectorStore).isInstanceOf(SimpleVectorStore.class);
    }
}
