package ma.logitrack.gateway.config;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;

@Component
@Order(-2)
public class UpstreamUnavailableHandler implements WebExceptionHandler {

    private static final byte[] RESPONSE_BODY = """
            {
              "title": "Service Unavailable",
              "status": 503,
              "detail": "Upstream service is unreachable."
            }
            """.getBytes(StandardCharsets.UTF_8);

    @Override
    public Mono<Void> handle(
            ServerWebExchange exchange,
            Throwable exception
    ) {
        Throwable cause =
                NestedExceptionUtils.getMostSpecificCause(exception);

        if (!(cause instanceof ConnectException)) {
            return Mono.error(exception);
        }

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(exception);
        }

        var response = exchange.getResponse();

        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        response.getHeaders().setContentType(
                MediaType.APPLICATION_PROBLEM_JSON
        );
        response.getHeaders().setContentLength(RESPONSE_BODY.length);

        return response.writeWith(Mono.just(
                response.bufferFactory().wrap(RESPONSE_BODY)
        ));
    }
}
