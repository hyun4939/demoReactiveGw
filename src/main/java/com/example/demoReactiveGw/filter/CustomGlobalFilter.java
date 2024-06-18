package com.example.demoReactiveGw.filter;

import com.example.demoReactiveGw.filter.config.FilterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.CacheRequestBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Slf4j
public class CustomGlobalFilter  implements GlobalFilter, Ordered{
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("GG custom global filter");
        ServerHttpRequest request = exchange.getRequest();

        if( "/hsvc/test2".equals( request.getURI().getPath() )) {
            ServerHttpRequest newRequest = exchange.getRequest().mutate().path("/test").build();

            return chain.filter(exchange.mutate().request(newRequest).build());
        }

        String body = exchange.getAttribute(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);

        var t = request.getHeaders().getFirst("test2");
        log.info("custom Filter baseMessage : t: {} ,body : {}",t , body);




        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
