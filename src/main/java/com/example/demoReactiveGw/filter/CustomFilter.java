package com.example.demoReactiveGw.filter;

import com.example.demoReactiveGw.filter.config.FilterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<FilterConfig> {
    public CustomFilter(){super(FilterConfig.class);}

    @Override
    public GatewayFilter apply(FilterConfig config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            String str = exchange.getAttribute(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);

            resolveBodyFromRequest(request);

            var t = request.getHeaders().getFirst("test");
            log.info("custom Filter baseMessage : {} , header  {}", config.getBaseMessage(), t);
            t = request.getHeaders().getFirst("test2");
            log.info("custom Filter baseMessage : {} , header test2 {}, body : {}", config.getBaseMessage(), t, str);

            if(config.isPreLogger()){
                log.info("custom Filter Start : request id -> {}", request.getId());
            }

            // Global Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("custom Filter End : response code -> {}", response.getStatusCode());
                if(config.isPostLogger()) {
                   //exchange.
                    log.info("-custom Filter End : response code -> {}", response.getStatusCode());
                }
            }));
        };
    }
    public static class Config{
        //Put the configuration properties
    }



    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest){
        //Get the request body
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        StringBuilder sb = new StringBuilder();
        var db = body.buffer();

        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            String bodyString2 = new String(bytes, StandardCharsets.UTF_8);
            log.info("===> {}",bodyString2);
            buffer.readPosition(0);
           // buffer.write(bytes);

//            DataBufferUtils.release(buffer);
//            String bodyString = new String(bytes, StandardCharsets.UTF_8);
//            sb.append(bodyString);
        });



        return sb.toString();

    }
}
