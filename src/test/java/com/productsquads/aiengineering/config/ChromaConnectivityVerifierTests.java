package com.productsquads.aiengineering.config;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class ChromaConnectivityVerifierTests {

    @Test
    void callsTheConfiguredChromaHeartbeat() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        ChromaConnectivityVerifier verifier = new ChromaConnectivityVerifier(properties(), builder);
        server.expect(requestTo("http://localhost:8000/api/v2/heartbeat"))
                .andRespond(withSuccess());

        assertThatCode(verifier::verify).doesNotThrowAnyException();
        server.verify();
    }

    @Test
    void failsWithoutLeakingConnectionCredentialsWhenHeartbeatFails() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        ChromaConnectivityVerifier verifier = new ChromaConnectivityVerifier(properties(), builder);
        server.expect(requestTo("http://localhost:8000/api/v2/heartbeat"))
                .andRespond(withServerError());

        assertThatThrownBy(verifier::verify)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Chroma connectivity check failed for the configured host and port");
        server.verify();
    }

    private static RagInfrastructureProperties properties() {
        return new RagInfrastructureProperties(
                new RagInfrastructureProperties.Chroma(
                        "local",
                        URI.create("http://localhost"),
                        8000,
                        "test-collection",
                        false,
                        true),
                new RagInfrastructureProperties.Embedding(
                        "ollama",
                        "test-embedding-model",
                        URI.create("http://localhost:11434")));
    }
}
