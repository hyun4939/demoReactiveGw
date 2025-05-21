package com.example.demoReactiveGw.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import reactor.netty.http.client.HttpClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.lettuce.core.RedisException;

import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.function.Supplier;

// Ensure that a bean of this type is conditionally created based on properties.
public class RedisTrustStoreHttpClientCustomizer implements HttpClientCustomizer {

    private static final Logger log = LoggerFactory.getLogger(RedisTrustStoreHttpClientCustomizer.class);

    // Functional interface for providing a Lettuce connection. This helps in mocking.
    @FunctionalInterface
    public interface LettuceConnectionProvider extends Supplier<StatefulRedisConnection<String, String>> {
    }

    private final String jksKeyInRedis;
    private final String jksPassword;
    private final LettuceConnectionProvider lettuceConnectionProvider;


    // Constructor for Spring's dependency injection
    public RedisTrustStoreHttpClientCustomizer(
            @Value("${spring.cloud.gateway.redis-truststore.redis.host:localhost}") String redisHost,
            @Value("${spring.cloud.gateway.redis-truststore.redis.port:6379}") int redisPort,
            @Value("${spring.cloud.gateway.redis-truststore.redis.password:#{null}}") String redisPassword,
            @Value("${spring.cloud.gateway.redis-truststore.redis.key}") String jksKeyInRedis,
            @Value("${spring.cloud.gateway.redis-truststore.jks-password}") String jksPassword) {
        this(jksKeyInRedis, jksPassword, () -> {
            RedisURI.Builder redisURIBuilder = RedisURI.builder()
                    .withHost(redisHost)
                    .withPort(redisPort);
            if (redisPassword != null && !redisPassword.isEmpty()) {
                redisURIBuilder.withPassword(redisPassword.toCharArray());
            }
            RedisURI redisURI = redisURIBuilder.build();
            RedisClient redisClient = RedisClient.create(redisURI);
            return redisClient.connect();
        });
        log.info("RedisTrustStoreHttpClientCustomizer (Spring constructor) initialized with Redis host: {}, port: {}, JKS key: {}",
                redisHost, redisPort, this.jksKeyInRedis);
        if (redisPassword != null && !redisPassword.isEmpty()) {
            log.info("Redis password is configured.");
        } else {
            log.info("Redis password is NOT configured.");
        }
        if (this.jksPassword == null || this.jksPassword.isEmpty()) {
            log.warn("JKS Password is null or empty. TrustStore loading might fail if the JKS is password-protected.");
        }
    }

    // Constructor for testing, allowing LettuceConnectionProvider injection
    public RedisTrustStoreHttpClientCustomizer(String jksKeyInRedis, String jksPassword, LettuceConnectionProvider lettuceConnectionProvider) {
        this.jksKeyInRedis = jksKeyInRedis;
        this.jksPassword = jksPassword;
        this.lettuceConnectionProvider = lettuceConnectionProvider;
        log.info("RedisTrustStoreHttpClientCustomizer (Test constructor) initialized with JKS key: {}", this.jksKeyInRedis);
    }


    @Override
    public HttpClient customize(HttpClient httpClient) {
        log.info("Attempting to customize HttpClient with Redis-based TrustStore using key: {}", this.jksKeyInRedis);
        try {
            String base64Jks = fetchBase64JksFromRedis(this.jksKeyInRedis);
            if (base64Jks == null) { // Key not found or Redis error already logged by fetchBase64JksFromRedis
                log.warn("Base64 JKS data not found or could not be fetched from Redis for key: {}. HttpClient will not be customized.", this.jksKeyInRedis);
                return httpClient; // Error already logged by called methods or this method.
            }

            byte[] jksData = decodeJks(base64Jks); // Can throw IllegalArgumentException

            TrustManagerFactory trustManagerFactory = createTrustManagerFactory(jksData, this.jksPassword); // Can throw various exceptions

            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(trustManagerFactory)
                    .build();

            log.info("Successfully configured HttpClient with SSLContext from Redis TrustStore for key: {}", this.jksKeyInRedis);
            return httpClient.secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
        } catch (IllegalArgumentException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            log.error("Error processing JKS data for HttpClient customization with key '{}': {}. HttpClient will not be customized.",
                    this.jksKeyInRedis, e.getMessage(), e);
            return httpClient;
        } catch (RedisException e) {
            // RedisException should be caught by fetchBase64JksFromRedis, but as a safeguard:
            log.error("Redis connection error during HttpClient customization for key '{}': {}. HttpClient will not be customized.",
                    this.jksKeyInRedis, e.getMessage(), e);
            return httpClient;
        } catch (Exception e) { // Catch any other unexpected exceptions
            log.error("Unexpected error during HttpClient customization with Redis TrustStore for key '{}': {}. HttpClient will not be customized.",
                    this.jksKeyInRedis, e.getMessage(), e);
            return httpClient;
        }
    }

    // Made package-private for easier testing if needed, though spies are preferred.
    String fetchBase64JksFromRedis(String key) throws RedisException {
        log.debug("Attempting to fetch Base64 JKS from Redis with key: {}", key);
        try (StatefulRedisConnection<String, String> connection = lettuceConnectionProvider.get()) {
            RedisStringCommands<String, String> syncCommands = connection.sync();
            String base64Jks = syncCommands.get(key);
            if (base64Jks != null && !base64Jks.isEmpty()) {
                log.info("Successfully fetched Base64 JKS data from Redis for key: {}", key);
                return base64Jks;
            } else {
                log.warn("No data found in Redis for key: {}, or data is empty.", key);
                return null;
            }
        } catch (RedisException e) {
            log.error("RedisException while fetching JKS from Redis for key '{}': {}", key, e.getMessage(), e);
            throw e; // Propagate RedisException
        }
        // Removed generic Exception catch to let RedisException propagate clearly.
    }

    // Made package-private for easier testing.
    byte[] decodeJks(String base64Jks) throws IllegalArgumentException {
        if (base64Jks == null || base64Jks.isEmpty()) {
            log.error("Base64 JKS string is null or empty. Cannot decode.");
            throw new IllegalArgumentException("Base64 JKS string cannot be null or empty.");
        }
        log.debug("Decoding Base64 JKS data (first 10 chars: {}...)", base64Jks.substring(0, Math.min(10, base64Jks.length())));
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Jks);
            log.info("Successfully decoded Base64 JKS data. Byte length: {}", decodedBytes.length);
            return decodedBytes;
        } catch (IllegalArgumentException e) {
            log.error("Error decoding Base64 JKS data: {}", e.getMessage(), e);
            throw e; // Re-throw the exception as per requirement
        }
    }

    // Made package-private for easier testing.
    TrustManagerFactory createTrustManagerFactory(byte[] jksBytes, String password)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, IllegalArgumentException {
        log.debug("Creating TrustManagerFactory from JKS bytes");
        if (jksBytes == null) {
            log.error("JKS bytes are null, cannot create TrustManagerFactory.");
            throw new IllegalArgumentException("JKS bytes cannot be null.");
        }
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            // Password can be null if the keystore is not password-protected.
            keyStore.load(new ByteArrayInputStream(jksBytes), password != null ? password.toCharArray() : null);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            log.info("TrustManagerFactory created successfully.");
            return trustManagerFactory;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            log.error("Error creating TrustManagerFactory (KeyStore/Algorithm/Certificate): {}", e.getMessage(), e);
            throw e;
        } catch (IOException e) { // Includes issues like "keystore password was incorrect" or "Keystore was tampered with, or password was incorrect"
            log.error("Error loading KeyStore (likely incorrect password, corrupted JKS, or not a JKS format): {}", e.getMessage(), e);
            throw e;
        }
    }
}
