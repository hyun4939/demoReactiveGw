//package com.example.demoReactiveGw.config;
//
//import org.reactivestreams.Publisher;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferFactory;
//import org.springframework.core.io.buffer.DefaultDataBufferFactory;
//import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import org.springframework.cloud.gateway.filter.factory.CacheRequestBodyGatewayFilterFactory;
//import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.web.server.ServerWebExchange;
//
//
//@Configuration
//public class GatewayConfig {
//
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
//                                           CacheRequestBodyGatewayFilterFactory cacheRequestBodyFilter) {
//        return builder.routes()
//                .route("cache_route", r -> r
//                        .path("/api/**")
//                        .filters(f -> f
//                                .filter(cacheRequestBodyFilter.apply(new CacheRequestBodyGatewayFilterFactory.Config()))
//                                .filter((exchange, chain) -> {
//                                    ServerHttpRequest request = exchange.getRequest();
//                                    ServerHttpResponse response = exchange.getResponse();
//
//                                    // 요청 데이터 로깅
//                                    Mono<Void> logRequest = exchange.getAttributes()
//                                            .containsKey(CachedBodyOutputMessage.class.getName())
//                                            ? logCachedRequest(exchange)
//                                            : Mono.empty();
//
//                                    // 응답 데이터 캐싱 및 로깅을 위한 ResponseDecorator
//                                    ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(response) {
//                                        @Override
//                                        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//                                            String cacheKey = exchange.getRequest().getPath().value();
//                                            if (body instanceof Flux) {
//                                                Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
//                                                return super.writeWith(fluxBody.map(dataBuffer -> {
//                                                    byte[] content = new byte[dataBuffer.readableByteCount()];
//                                                    dataBuffer.read(content);
//                                                    String bodyString = new String(content);
//
//                                                    // 응답 데이터 캐싱
//                                                    LocalResponseCache.INSTANCE.put(cacheKey, bodyString);
//
//                                                    // 응답 데이터 로깅
//                                                    System.out.println("Cached Response Body: " + bodyString);
//
//                                                    return exchange.getResponse().bufferFactory().wrap(content);
//                                                }));
//                                            }
//                                            return super.writeWith(body);
//                                        }
//                                    };
//
//                                    return logRequest.then(chain.filter(exchange.mutate().response(responseDecorator).build()));
//                                }))
//                        .uri("http://localhost:8080")) // 실제 서비스 URL로 변경 필요
//                .build();
//    }
//
//    private Mono<Void> logCachedRequest(ServerWebExchange exchange) {
//        CachedBodyOutputMessage cachedBody = exchange.getAttribute(CachedBodyOutputMessage.class.getName());
//        if (cachedBody != null) {
//            return cachedBody.getBody().map(dataBuffer -> {
//                byte[] bytes = new byte[dataBuffer.readableByteCount()];
//                dataBuffer.read(bytes);
//                String bodyString = new String(bytes);
//                System.out.println("Cached Request Body: " + bodyString);
//                return Mono.empty();
//            }).then();
//        }
//        return Mono.empty();
//    }
//
//
//}
//
