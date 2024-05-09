package com.example.demoReactiveGw.filter;

import com.example.demoReactiveGw.filter.config.FilterConfig;
import com.exampledemoReactiveGw.filter.config.FilterConfig;
//import org.junit.Test;
//import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
public class CustomFilterTest {

    private GatewayFilterChain filterChain = mock(GatewayFilterChain.class);

    @Test
    void testPublicFilter(){
        MockServerHttpRequest request = MockServerHttpRequest.get( "auth").build();
        MockServerHttpResponse response = new MockServerHttpResponse();

        CustomFilter customFilter = new CustomFilter();
        Mono<GatewayFilter> chain = (exchange, filterChain) -> {
            FilterConfig config = new FilterConfig();
            /* exchange.getAttributes().put("config", config); */
            return customFilter.apply(config);

        };

    }
}
