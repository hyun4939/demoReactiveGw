package com.example.demoReactiveGw.filter;

import com.example.demoReactiveGw.filter.config.FilterConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.mock;
@Slf4j
@ExtendWith(SpringExtension.class)
public class CustomFilterTest {

    private GatewayFilterChain filterChain = mock(GatewayFilterChain.class);

    @Test
    public void testPublicFilter(){
        MockServerHttpRequest request = MockServerHttpRequest.get( "/auth").build();

        MockServerHttpResponse response = new MockServerHttpResponse();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        CustomFilter customFilter = new CustomFilter();

        var filter = customFilter.apply(new FilterConfig());
        GatewayFilterChain filterChain = mock(GatewayFilterChain.class);
        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);

        Mono<Void> rtn = Mono.never();
        Mockito.when(filterChain.filter(captor.capture()))
                .thenReturn(rtn);

        Mockito.when(filterChain.filter(exchange).then(Mono.fromRunnable(() -> {
                }))).thenReturn(Mono.just("{}"));

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();
        var resultExchange = captor.getValue();

        // verify result exchange
        // assertEquals("status 200 equals "+resultExchange.getResponse().getStatusCode().value(),
        // getResponse().getStatusCode().value(), 200);

    }
}
