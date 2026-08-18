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
        "CHROMA_INITIALIZE_SCHEMA=false",
        "spring.autoconfigure.exclude=org.springframework.ai.vectorstore.chroma.autoconfigure.ChromaVectorStoreAutoConfiguration"
})
@ActiveProfiles({"ollama", "local-rag"})
class LocalRagProfileTests {

    @Autowired
    private RagInfrastructureProperties properties;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Test
    void createsLocalOllamaEmbeddingAndChromaConfiguration() {
        assertThat(properties.chroma().mode()).isEqualTo("local");
        assertThat(properties.chroma().host()).isEqualTo(URI.create("http://localhost"));
        assertThat(properties.embedding().provider()).isEqualTo("ollama");
        assertThat(properties.embedding().model()).isEqualTo("nomic-embed-text");
        assertThat(embeddingModel.getClass().getSimpleName()).contains("Ollama");
    }
}
