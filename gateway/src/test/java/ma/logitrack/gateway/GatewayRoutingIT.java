package ma.logitrack.gateway;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "management.server.port=0",
                "spring.cloud.gateway.server.webflux.httpclient.response-timeout=500ms"
        }
)
class GatewayRoutingIT {

    private static final WireMockServer FLEET =
            new WireMockServer(wireMockConfig().dynamicPort());

    static {
        FLEET.start();
    }

    @DynamicPropertySource
    static void routes(DynamicPropertyRegistry registry) {
        registry.add("app.routes.fleet-uri", FLEET::baseUrl);
    }

    @AfterAll
    static void stopFleet() {
        FLEET.stop();
    }

    @Value("${local.server.port}")
    private int port;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        FLEET.resetAll();

        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldForwardWithSamePathAndQuery_whenVehiclesRequested() {
        FLEET.stubFor(get(urlPathEqualTo("/api/v1/vehicles"))
                .willReturn(okJson("{\"content\":[]}")));

        client.get()
                .uri("/api/v1/vehicles?page=0&size=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray();

        FLEET.verify(getRequestedFor(
                urlEqualTo("/api/v1/vehicles?page=0&size=5")
        ));
    }

    @Test
    void shouldForwardSamePath_whenDriverSubPathRequested() {
        FLEET.stubFor(get(urlPathEqualTo("/api/v1/drivers/abc"))
                .willReturn(okJson("{\"id\":\"abc\"}")));

        client.get()
                .uri("/api/v1/drivers/abc")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("abc");

        FLEET.verify(getRequestedFor(
                urlEqualTo("/api/v1/drivers/abc")
        ));
    }

    @Test
    void shouldReturn404WithoutForwarding_whenNoRouteMatches() {
        client.get()
                .uri("/api/v1/unknown")
                .exchange()
                .expectStatus().isNotFound();

        FLEET.verify(0, anyRequestedFor(anyUrl()));
    }

    @Test
    void shouldReturn504WhenFleetResponseTimesOut() {
        FLEET.stubFor(get(urlPathEqualTo("/api/v1/vehicles"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(2000)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                              "content": []
                            }
                            """)));

        client.get()
                .uri("/api/v1/vehicles")
                .exchange()
                .expectStatus().isEqualTo(504);
    }
}
