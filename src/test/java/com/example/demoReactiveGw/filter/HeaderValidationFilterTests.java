package com.example.demoReactiveGw.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.test.BaseWebClientTests;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class HeaderValidationFilterTests extends BaseWebClientTests {

    @Autowired
    private WebTestClient webTestClient;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private CustomLoggingFilter customLoggingFilter;

    @InjectMocks
    private AnotherFilter anotherFilter;

    @BeforeEach
    public void setup() {
//        MockitoAnnotations.openMocks(this);

        WebClient webClient = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Mock response"));
    }


    @Autowired
    private HeaderValidationFilter headerValidationFilter;

    @Test
    void testValidHeader() {
        HeaderValidationFilter.Config config = new HeaderValidationFilter.Config();
        config.setHeaderName("X-Auth-Token");
        config.setExpectedValues(Arrays.asList("valid-token"));

        GatewayFilter filter = headerValidationFilter.apply(config);

        webTestClient.get()
                .uri("/test")
                .header(HttpHeaders.HOST, "www.example.com")
                .header("X-Auth-Token", "valid-token")
                .filter(filter)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testInvalidHeader() {
        HeaderValidationFilter.Config config = new HeaderValidationFilter.Config();
        config.setHeaderName("X-Auth-Token");
        config.setExpectedValues(Arrays.asList("valid-token"));

        GatewayFilter filter = headerValidationFilter.apply(config);

        webTestClient.get()
                .uri("/test")
                .header(HttpHeaders.HOST, "www.example.com")
                .header("X-Auth-Token", "invalid-token")
                .filter(filter)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
