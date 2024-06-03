package com.example.demoReactiveGw.filter;

import com.example.demoReactiveGw.filter.config.FilterConfig;
//import org.junit.Test;
//import org.junit.runner.RunWith;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Iterator;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static reactor.core.publisher.Mono.when;

@Slf4j
@RunWith(SpringRunner.class)
public class CustomFilterTest {

    private GatewayFilterChain filterChain = mock(GatewayFilterChain.class);

//    @Test
//    public void testPublicFilter(){
//        MockServerHttpRequest request = MockServerHttpRequest.get("/auth").build();
//        MockServerHttpResponse response = new MockServerHttpResponse();
//        MockServerWebExchange exchange = MockServerWebExchange.from(request);
//
//        CustomFilter customFilter = new CustomFilter();
//        var filter = customFilter.apply(new FilterConfig());
//
//        GatewayFilterChain filterChain = mock(GatewayFilterChain.class);
//        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
//
//        assertNotNull(exchange);
//        assertNotNull(filterChain);
//        assertNotNull(filter);
//        assertNotNull(captor);
//
//        Mono<Void> source = Mono.empty();
//        // Setup filterChain to return the source Mono when filter is called
//        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(source);
//
//        // Call filter and capture the exchange
//        filter.filter(exchange, filterChain);
//
//        // Ensure the filterChain.filter method is called with the captured exchange
//        verify(filterChain).filter(captor.capture());
//
//        StepVerifier.create(filter.filter(exchange, filterChain))
//                .expectComplete()
//                .verify();
//
//        ServerWebExchange resultExchange = captor.getValue();
//
//        // Verify result exchange status code
//        assertEquals(200, resultExchange.getResponse().getStatusCode().value());
//    }

    @Test
    public void testPublicFilter() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/auth").build();
        MockServerHttpResponse response = new MockServerHttpResponse();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        CustomFilter customFilter = new CustomFilter();
        var filter = customFilter.apply(new FilterConfig());

        GatewayFilterChain filterChain = mock(GatewayFilterChain.class);
        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);

        // Check assertions for objects that should not be null
//        assertNotNull(exchange,"exchange null");
//        assertNotNull(filterChain,"filterChain null");
//        assertNotNull(filter,"filter null");
//        assertNotNull(captor,"captor null");

        // Ensure source is not null
//        Mono<Void> source = Mono.empty();
        Mono<Void> source = Mono.empty().doOnNext(v -> { /* do something */ }).then();
        assertNotNull(source);

        // Setup filterChain to return the source Mono when filter is called
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(source);

        // Call filter and capture the exchange
        //filter.filter(exchange, filterChain);

        // Ensure the filterChain.filter method is called with the captured exchange


        // StepVerifier to verify the Mono completes successfully
        StepVerifier.create(filter.filter(exchange, filterChain))
                .expectComplete()
                .verify();
        verify(filterChain).filter(captor.capture());
        // Get the captured exchange
        ServerWebExchange resultExchange = captor.getValue();

        // Ensure resultExchange is not null before accessing it
        assertNotNull(resultExchange);

        // Verify result exchange status code
        assertEquals(200, resultExchange.getResponse().getStatusCode().value());
    }


}
