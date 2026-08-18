package com.productsquads.aiengineering.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@ConditionalOnProperty(prefix = "app.rag.chroma", name = "check-on-startup", havingValue = "true")
public class ChromaConnectivityVerifier implements ApplicationRunner {

    private static final String HEARTBEAT_PATH = "/api/v2/heartbeat";

    private final RestClient restClient;

    public ChromaConnectivityVerifier(
            RagInfrastructureProperties properties,
            RestClient.Builder restClientBuilder) {
        RagInfrastructureProperties.Chroma chroma = properties.chroma();
        String baseUrl = UriComponentsBuilder.fromUri(chroma.host())
                .port(chroma.port())
                .build()
                .toUriString();
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public void run(ApplicationArguments args) {
        verify();
    }

    void verify() {
        try {
            restClient.get()
                    .uri(HEARTBEAT_PATH)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    "Chroma connectivity check failed for the configured host and port",
                    exception);
        }
    }
}
