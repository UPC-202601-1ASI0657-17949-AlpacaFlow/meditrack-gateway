package com.alpacaflow.meditrack.meditrackgateway.infrastructure.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * IAM and Organization also emit CORS headers. The gateway adds its own, so browsers
 * see duplicated Access-Control-* values and block the request from Firebase.
 */
@Component
public class DedupeCorsResponseHeadersFilter implements GlobalFilter, Ordered {

    private static final List<String> CORS_HEADERS = List.of(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Access-Control-Allow-Methods",
            "Access-Control-Allow-Headers",
            "Access-Control-Expose-Headers",
            "Access-Control-Max-Age"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();
            CORS_HEADERS.forEach(name -> retainFirstHeaderValue(headers, name));
        }));
    }

    private static void retainFirstHeaderValue(HttpHeaders headers, String name) {
        List<String> values = headers.get(name);
        if (values == null || values.isEmpty()) {
            return;
        }
        var first = values.get(0);
        if (first.contains(",")) {
            first = first.split(",")[0].trim();
        }
        headers.set(name, first);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
