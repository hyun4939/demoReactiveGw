package com.example.demoReactiveGw.filter;


import com.example.demoReactiveGw.config.ResponseDecorator;
import com.example.demoReactiveGw.filter.config.FilterConfig;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class RequestResponseLoggingFilter extends AbstractGatewayFilterFactory<FilterConfig> {
    public RequestResponseLoggingFilter(){super(FilterConfig.class);}

    @Override
    public GatewayFilter apply(FilterConfig config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 요청 데이터 로깅
            Mono<Void> logRequest = exchange.getAttributes()
                    .containsKey(CachedBodyOutputMessage.class.getName())
                    ? logCachedRequest(exchange)
                    : Mono.empty();

            // 응답 데이터 캐싱 및 로깅을 위한 ResponseDecorator
            ServerHttpResponseDecorator responseDecorator = new ResponseDecorator(response,exchange) ;

            return logRequest.then(chain.filter(exchange.mutate().response(responseDecorator).build()));
        };
    }
    public static class Config{
        //Put the configuration properties
    }

    private Mono<Void> logCachedRequest(ServerWebExchange exchange) {
        CachedBodyOutputMessage cachedBody = exchange.getAttribute(CachedBodyOutputMessage.class.getName());
        if (cachedBody != null) {
            return cachedBody.getBody().map(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                String bodyString = new String(bytes);
                System.out.println("Cached Request Body: " + bodyString);
                return Mono.empty();
            }).then();
        }
        return Mono.empty();
    }
}
