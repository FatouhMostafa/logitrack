package ma.logitrack.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.net.ServerSocket;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "management.server.port=0"
)
class GatewayUpstreamDownIT {

    private static final int UNAVAILABLE_PORT = findFreePort();

    @DynamicPropertySource
    static void configureFleetUri(DynamicPropertyRegistry registry) {
        registry.add(
                "app.routes.fleet-uri",
                () -> "http://localhost:" + UNAVAILABLE_PORT
        );
    }

    @Value("${local.server.port}")
    private int port;

    @Test
    void shouldReturn503ProblemDetails_whenFleetIsUnavailable() {
        WebTestClient client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        client.get()
                .uri("/api/v1/vehicles")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectHeader()
                .contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                )
                .expectBody()
                .jsonPath("$.title").isEqualTo("Service Unavailable")
                .jsonPath("$.status").isEqualTo(503)
                .jsonPath("$.detail")
                .isEqualTo("Upstream service is unreachable.");
    }

    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not allocate a port for the test.",
                    exception
            );
        }
    }
}
