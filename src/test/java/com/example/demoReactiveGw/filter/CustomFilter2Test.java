//package com.example.demoReactiveGw.filter;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@AutoConfigureWebTestClient
//public class CustomFilter2Test {
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    @Mock
//    private WebClient.Builder webClientBuilder;
//
//    @InjectMocks
//    private CustomLoggingFilter customLoggingFilter;
//
//    @InjectMocks
//    private AnotherFilter anotherFilter;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//
//        WebClient webClient = mock(WebClient.class);
//        WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
//        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Mock response"));
//    }
//
//    @Configuration
//    static class TestConfig {
//        @Bean
//        public RouteLocator testRouteLocator(RouteLocatorBuilder builder, CustomLoggingFilter customLoggingFilter, AnotherFilter anotherFilter) {
//            return builder.routes()
//                    .route("test_route", r -> r.path("/test")
//                            .filters(f -> {
//                                f.filter(customLoggingFilter.apply(new CustomLoggingFilter.Config()));
//                                f.filter(anotherFilter.apply(new AnotherFilter.Config()));
//                                return f;
//                            })
//                            .uri("http://httpbin.org:80"))
//                    .build();
//        }
//    }
//
//    @Test
//    public void testCustomLoggingFilter() {
//        webTestClient.get().uri("/test")
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().exists("Date")
//                .expectBody()
//                .jsonPath("$.headers.Host").isNotEmpty();
//
//        // Optionally, check logs or other effects specific to CustomLoggingFilter
//    }
//}