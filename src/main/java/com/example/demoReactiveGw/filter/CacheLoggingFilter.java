package com.example.demoReactiveGw.filter;

import com.example.demoReactiveGw.config.LocalResponseCache;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CacheLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 요청 처리
        ServerHttpRequestDecorator requestDecorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public Flux<DataBuffer> getBody() {
                return super.getBody().doOnNext(dataBuffer -> {
                    byte[] content = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(content);
                    String bodyString = new String(content, StandardCharsets.UTF_8);
                    log.info("Request Body: " + bodyString);
                    LocalResponseCache.INSTANCE.put(getPath().value() + "_request", bodyString);
                });
            }
        };

        // 응답 처리
        ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(exchange.getResponse()) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<DataBuffer> fluxBody = (Flux<DataBuffer>) body;
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        DataBuffer joinedBuffer = exchange.getResponse().bufferFactory().join(dataBuffers);
                        byte[] content = new byte[joinedBuffer.readableByteCount()];
                        joinedBuffer.read(content);
                        String bodyString = new String(content, StandardCharsets.UTF_8);
                        log.info("Response Body: " + bodyString);
                        LocalResponseCache.INSTANCE.put(exchange.getRequest().getPath().value() + "_response", bodyString);
                        return exchange.getResponse().bufferFactory().wrap(content);
                    }));
                }
                return super.writeWith(body);
            }
        };

        return chain.filter(exchange.mutate().request(requestDecorator).response(responseDecorator).build());
    }

    @Override
    public int getOrder() {
        return -1; // 높은 우선순위 설정
    }
}

// LocalResponseCache 클래스는 이전과 동일
