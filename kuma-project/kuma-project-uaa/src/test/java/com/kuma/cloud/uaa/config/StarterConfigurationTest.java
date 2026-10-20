package com.kuma.cloud.uaa.config;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2EndpointProperties;
import com.kuma.boot.totp.code.CodeVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class StarterConfigurationTest {
    @Test
    void explicitImportsProvideTheRateLimitAndDuplicateSubmissionAspects() {
        new ApplicationContextRunner()
                .withBean(org.redisson.api.RedissonClient.class,
                        () -> mock(org.redisson.api.RedissonClient.class))
                .withBean(org.springframework.data.redis.core.StringRedisTemplate.class,
                        () -> mock(org.springframework.data.redis.core.StringRedisTemplate.class))
                .withUserConfiguration(RateLimitConfiguration.class, IdempotentConfiguration.class)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(
                            com.kuma.boot.ratelimit.ratelimitredis.RedisRateLimiterAspect.class);
                    assertThat(context).hasSingleBean(
                            com.kuma.boot.idempotent.idempotetduplicate.PreventDuplicateSubmitAspect.class);
                });
    }

    @Test
    void explicitImportsProvideRedisTotpAndEnforceMethodPermissions() {
        new ApplicationContextRunner()
                .withConfiguration(org.springframework.boot.autoconfigure.AutoConfigurations.of(
                        org.springframework.cloud.autoconfigure.RefreshAutoConfiguration.class))
                .withUserConfiguration(RedisConfiguration.class, TotpConfiguration.class,
                        ExecutionConfiguration.class, FrameworkSecurityConfiguration.class, Fixtures.class)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(RedisRepository.class);
                    assertThat(context).hasSingleBean(CodeVerifier.class);
                    assertThat(context).hasSingleBean(OAuth2EndpointProperties.class);
                    assertThat(context).hasSingleBean(ThreadPoolTaskExecutor.class);
                    assertThat(context).doesNotHaveBean("oauth2ResourceSecurityFilterChain");
                    var service = context.getBean(SecuredService.class);
                    var oldContext = SecurityContextHolder.getContext();
                    var securityContext = SecurityContextHolder.createEmptyContext();
                    SecurityContextHolder.setContext(securityContext);
                    try {
                        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(
                                "reader", "", List.of(new SimpleGrantedAuthority("read"))));
                        assertThat(service.read()).isEqualTo("allowed");
                        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(
                                "guest", "", List.of(new SimpleGrantedAuthority("other"))));
                        assertThatThrownBy(service::read).isInstanceOf(AccessDeniedException.class);
                    } finally {
                        SecurityContextHolder.setContext(oldContext);
                    }
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class Fixtures {
        @Bean
        RedisConnectionFactory redisConnectionFactory() {
            return mock(RedisConnectionFactory.class, org.mockito.Answers.RETURNS_DEEP_STUBS);
        }

        @Bean
        SecuredService securedService() { return new SecuredService(); }
    }

    static class SecuredService {
        @Authorize("read")
        public String read() { return "allowed"; }
    }
}
