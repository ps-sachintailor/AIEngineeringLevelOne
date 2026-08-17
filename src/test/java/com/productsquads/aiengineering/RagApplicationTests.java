package com.productsquads.aiengineering;

import static org.assertj.core.api.Assertions.assertThat;

import com.productsquads.aiengineering.config.AiModelProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.health.contributor.Status;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
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
    void externalAiIsDisabledByDefault() {
        assertThat(aiModelProperties.provider()).isEqualTo("none");
        assertThat(aiModelProperties.model()).isNotBlank();
    }
}
