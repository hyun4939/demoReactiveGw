package com.example.demoReactiveGw.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        prefix = "spring.cloud.gateway.redis-truststore",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class RedisTrustStoreClientAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RedisTrustStoreClientAutoConfiguration.class);

    @Bean
    public RedisTrustStoreHttpClientCustomizer redisTrustStoreHttpClientCustomizer(
            @Value("${spring.cloud.gateway.redis-truststore.redis.host:localhost}") String redisHost,
            @Value("${spring.cloud.gateway.redis-truststore.redis.port:6379}") int redisPort,
            @Value("${spring.cloud.gateway.redis-truststore.redis.password:#{null}}") String redisPassword,
            @Value("${spring.cloud.gateway.redis-truststore.redis.key}") String jksKeyInRedis,
            @Value("${spring.cloud.gateway.redis-truststore.jks-password}") String jksPassword) {
        
        log.info("ConditionalOnProperty for RedisTrustStore matched. Creating RedisTrustStoreHttpClientCustomizer bean.");
        return new RedisTrustStoreHttpClientCustomizer(redisHost, redisPort, redisPassword, jksKeyInRedis, jksPassword);
    }
}
