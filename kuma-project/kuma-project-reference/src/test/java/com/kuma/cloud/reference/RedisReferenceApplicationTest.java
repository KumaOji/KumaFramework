package com.kuma.cloud.reference;

import static org.junit.jupiter.api.Assertions.*;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.cloud.reference.domain.dto.CreateOrderDTO;
import com.kuma.cloud.reference.domain.enums.DataScope;
import com.kuma.cloud.reference.service.OrderService;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

@EnabledIfEnvironmentVariable(named = "REFERENCE_REDIS_TEST_PASSWORD", matches = ".+")
@TestPropertySource(properties = {
        "kuma.boot.cache.redis.enabled=true",
        "spring.data.redis.client-type=lettuce",
        "spring.data.redis.host=${REFERENCE_REDIS_HOST:172.23.89.45}",
        "spring.data.redis.port=${REFERENCE_REDIS_PORT:31281}",
        "spring.data.redis.username=${REFERENCE_REDIS_USERNAME:default}",
        "spring.data.redis.password=${REFERENCE_REDIS_TEST_PASSWORD}",
        "spring.data.redis.database=${REFERENCE_REDIS_DATABASE:0}",
        "reference.redis.order-cache-ttl=30s",
        "management.health.redis.enabled=true"
})
class RedisReferenceApplicationTest extends ReferenceApplicationTest {
    private static final String PREFIX = "kuma-reference:test:" + UUID.randomUUID();

    @DynamicPropertySource
    static void cachePrefix(DynamicPropertyRegistry properties) {
        properties.add("reference.redis.key-prefix", () -> PREFIX);
    }

    @Autowired OrderService service;
    @Autowired RedisRepository redis;

    @Test
    void cachesOnlyConfirmedOrdersWithScopeAndExpiry() throws Exception {
        try (var client = HttpClient.newHttpClient()) {
            var health = client.send(HttpRequest.newBuilder(
                    URI.create("http://127.0.0.1:" + port + "/actuator/health/redis")).build(),
                    HttpResponse.BodyHandlers.ofString());
            assertEquals(200, health.statusCode(), health.body());
        }

        var request = new CreateOrderDTO("REDIS-001", "Redis sample", new BigDecimal("9.99"), 1);
        var order = service.create(request, DataScope.MAIN);
        String key = PREFIX + ":orders:v1:MAIN:" + order.id();
        assertEquals("CREATED", service.get(order.id(), DataScope.MAIN).status());
        assertEquals(Boolean.FALSE, redis.opsForValue().getOperations().hasKey(key));

        try {
            var confirmed = service.confirm(order.id(), DataScope.MAIN);
            assertEquals(confirmed, service.get(order.id(), DataScope.MAIN));
            assertInstanceOf(String.class, redis.opsForValue().get(key));
            long ttl = redis.getExpire(key);
            assertTrue(ttl > 0 && ttl <= 30);
            assertFalse(redis.hasKey(PREFIX + ":orders:v1:MYSQL:" + order.id()));
            assertEquals(confirmed, service.get(order.id(), DataScope.MAIN));
        } finally {
            redis.del(key);
        }
    }
}
