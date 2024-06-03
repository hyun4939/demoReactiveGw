package com.example.demoReactiveGw.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class HeaderValidationFilter extends AbstractGatewayFilterFactory<HeaderValidationFilter.Config> {

    public HeaderValidationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (!headerContainsValue(request, config.getHeaderName(), config.getExpectedValues())) {
                // 헤더가 유효하지 않으면 에러 처리
                return handleInvalidHeader(exchange, config);
            }
            return chain.filter(exchange);
        };
    }

    private boolean headerContainsValue(ServerHttpRequest request, String headerName, List<String> expectedValues) {
        List<String> headerValues = request.getHeaders().get(headerName);
        if (headerValues == null) {
            return false;
        }
        return headerValues.stream().anyMatch(expectedValues::contains);
    }

    private GatewayFilter handleInvalidHeader(ServerWebExchange exchange, Config config) {
        // 에러 처리 로직을 구현
        return exchange -> exchange.getPrincipal().block();
    }

    public static class Config {
        private String headerName;
        private List<String> expectedValues;

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        public List<String> getExpectedValues() {
            return expectedValues;
        }

        public void setExpectedValues(List<String> expectedValues) {
            this.expectedValues = expectedValues;
        }
    }
}
