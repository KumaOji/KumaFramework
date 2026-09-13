package com.kuma.cloud.blog.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtDenylistServiceTest {

    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private JwtDenylistService denylistService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        denylistService = new JwtDenylistService(redisTemplate, Duration.ofDays(30));
    }

    @Test
    void revokeRefreshTokenWritesHashedKey() {
        denylistService.revokeRefreshToken("refresh-abc");

        verify(valueOperations).set(
                startsWith("blog:refresh:deny:"),
                eq("1"),
                eq(Duration.ofDays(30)));
    }

    @Test
    void isRefreshTokenRevokedReadsHashedKey() {
        when(redisTemplate.hasKey(startsWith("blog:refresh:deny:")))
                .thenReturn(true);

        assertThat(denylistService.isRefreshTokenRevoked("refresh-abc")).isTrue();
    }
}
