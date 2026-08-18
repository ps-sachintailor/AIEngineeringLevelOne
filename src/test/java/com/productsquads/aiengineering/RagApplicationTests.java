package com.productsquads.aiengineering;

import static org.assertj.core.api.Assertions.assertThat;

import com.productsquads.aiengineering.config.AiModelProperties;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.health.contributor.Status;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties =
        "spring.autoconfigure.exclude=org.springframework.ai.vectorstore.chroma.autoconfigure.ChromaVectorStoreAutoConfiguration")
class RagApplicationTests {

    @Autowired
    private HealthEndpoint healthEndpoint;

    @Autowired
    private AiModelProperties aiModelProperties;

    @Test
    void applicationStartsAndReportsHealthy() {
        assertThat(healthEndpoint.health().getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void localOllamaIsConfiguredByDefault() {
        assertThat(aiModelProperties.provider()).isEqualTo("ollama");
        assertThat(aiModelProperties.model()).isEqualTo("llama3.2:3b");
        assertThat(aiModelProperties.baseUrl()).isEqualTo(URI.create("http://localhost:11434"));
    }
}
