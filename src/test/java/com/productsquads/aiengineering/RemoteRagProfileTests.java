package com.productsquads.aiengineering;

import static org.assertj.core.api.Assertions.assertThat;

import com.productsquads.aiengineering.config.RagInfrastructureProperties;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "CHROMA_HOST=https://api.trychroma.com",
        "CHROMA_PORT=443",
        "CHROMA_KEY_TOKEN=test-only-chroma-key",
        "CHROMA_TENANT_NAME=test-tenant",
        "CHROMA_DATABASE_NAME=test-database",
        "CHROMA_INITIALIZE_SCHEMA=false",
        "EMBEDDING_OPENAI_BASE_URL=https://models.example.test/v1",
        "EMBEDDING_OPENAI_API_KEY=test-only-embedding-key",
        "EMBEDDING_OPENAI_MODEL=test-embedding-model",
        "spring.autoconfigure.exclude=org.springframework.ai.vectorstore.chroma.autoconfigure.ChromaVectorStoreAutoConfiguration"
})
@ActiveProfiles({"ollama", "remote-rag"})
class RemoteRagProfileTests {

    @Autowired
    private RagInfrastructureProperties properties;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Test
    void createsRemoteOpenAiCompatibleEmbeddingAndChromaConfiguration() {
        assertThat(properties.chroma().mode()).isEqualTo("cloud");
        assertThat(properties.chroma().host())
                .isEqualTo(URI.create("https://api.trychroma.com"));
        assertThat(properties.embedding().provider()).isEqualTo("openai-compatible");
        assertThat(properties.embedding().model()).isEqualTo("test-embedding-model");
        assertThat(embeddingModel.getClass().getSimpleName()).contains("OpenAi");
    }
}
