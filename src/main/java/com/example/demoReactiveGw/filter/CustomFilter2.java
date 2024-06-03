package com.example.demoReactiveGw.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomFilter2 extends AbstractGatewayFilterFactory<CustomFilter2.Config> {

    private static final Logger logger = LoggerFactory.getLogger(CustomFilter2.class);

    public CustomFilter2() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            logger.info("Custom logging filter: request path = {}", exchange.getRequest().getPath());
            return chain.filter(exchange).then(Mono.fromRunnable(() ->
                    logger.info("Custom logging filter: response status = {}", exchange.getResponse().getStatusCode())));
        };
    }

    public static class Config {
        // Put the configuration properties here
    }
}

