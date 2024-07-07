package com.example.demoReactiveGw.config;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ResponseDecorator extends ServerHttpResponseDecorator {
    ServerWebExchange exchange;

    public ResponseDecorator(ServerHttpResponse delegate, ServerWebExchange exchange) {
        super(delegate);
        this.exchange = exchange;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        String cacheKey = exchange.getRequest().getPath().value();
        if (body instanceof Flux) {
            Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
            return super.writeWith(fluxBody.map(dataBuffer -> {
                byte[] content = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(content);
                String bodyString = new String(content);

                // 응답 데이터 캐싱
                LocalResponseCache.INSTANCE.put(cacheKey, bodyString);

                // 응답 데이터 로깅
                System.out.println("Cached Response Body: " + bodyString);

                return exchange.getResponse().bufferFactory().wrap(content);
            }));
        }
        return super.writeWith(body);
    }
}

