package com.twa.flights.api.itineraries.search.connector.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public final class ConnectorFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorFilter.class);

    public static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            LOGGER.debug("@@ ------------ Request ----------- ");
            LOGGER.debug("{} {}", request.method().name(), request.url());
            request.headers().forEach((name, values) -> {
                values.forEach(value -> LOGGER.debug("{}={}", name, value));
            });
            return Mono.just(request);
        });
    }

    public static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            LOGGER.debug("@@ ------------ Response ---------- ");
            LOGGER.debug("Status code: {} ({})", response.statusCode().value(),
                    response.statusCode().getReasonPhrase());
            response.headers().asHttpHeaders().forEach((name, values) -> {
                values.forEach(value -> LOGGER.debug("{}={}", name, value));
            });
            return Mono.just(response);
        });
    }

    private ConnectorFilter() {
    }
}
