package com.kuma.boot.websocket.stomp.autoconfigure;

import com.kuma.boot.websocket.stomp.interceptor.WebSocketChannelInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StompAutoConfigurationTest {
    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(MessageWebSocketAutoConfiguration.class));

    @Test
    void entryPointRemainsOptInThroughExplicitApplicationImports() {
        assertThat(org.springframework.boot.context.annotation.ImportCandidates.load(
                org.springframework.boot.autoconfigure.AutoConfiguration.class, getClass().getClassLoader()))
                .doesNotContain(MessageWebSocketAutoConfiguration.class.getName());
    }

    @Test
    void authenticatedChannelAcceptsStandardPrincipalsAndRejectsMissingIdentity() {
        var interceptor = new WebSocketChannelInterceptor();
        var headers = org.springframework.messaging.simp.stomp.StompHeaderAccessor.create(
                org.springframework.messaging.simp.stomp.StompCommand.CONNECT);
        headers.setUser(() -> "standard-principal");
        var authenticated = org.springframework.messaging.support.MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
        assertThat(interceptor.preSend(authenticated, null)).isSameAs(authenticated);
        var anonymous = org.springframework.messaging.support.MessageBuilder.createMessage(new byte[0],
                org.springframework.messaging.simp.stomp.StompHeaderAccessor.create(
                        org.springframework.messaging.simp.stomp.StompCommand.CONNECT).getMessageHeaders());
        assertThat(interceptor.preSend(anonymous, null)).isNull();
    }

    @Test
    void disabledByDefaultAndDoesNotCreateBrokerOrScheduler() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean(SimpMessagingTemplate.class);
            assertThat(context).doesNotHaveBean(ThreadPoolTaskScheduler.class);
        });
    }

    @Test
    void enabledWithoutRedisAndBusinessCustomizerIsApplied() {
        runner.withUserConfiguration(BusinessEndpoint.class)
                .withPropertyValues("kuma.boot.websocket.stomp.enabled=true",
                        "kuma.boot.websocket.stomp.endpoint=/ws/chat",
                        "kuma.boot.websocket.stomp.broker-prefixes=/topic",
                        "kuma.boot.websocket.stomp.native-endpoint-enabled=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SimpMessagingTemplate.class);
                    assertThat(context).doesNotHaveBean("webSocketClusterProcessor");
                    assertThat(context).doesNotHaveBean(WebSocketChannelInterceptor.class);
                    assertThat(context.getBean(AtomicInteger.class).get()).isEqualTo(1);
                    assertThat(context.getBean("stompWebSocketHandlerMapping")).isNotNull();
                });
    }

    @Test
    void principalAuthenticationRequiresAnExplicitTokenResolver() {
        runner.withPropertyValues("kuma.boot.websocket.stomp.enabled=true",
                        "kuma.boot.websocket.stomp.principal-authentication-enabled=true")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasStackTraceContaining("BearerTokenResolver");
                });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableWebMvc
    static class BusinessEndpoint {
        @Bean AtomicInteger customizations() { return new AtomicInteger(); }
        @Bean WebSocketStompEndpointCustomizer endpointCustomizer(AtomicInteger count) {
            return endpoint -> {
                count.incrementAndGet();
                endpoint.addInterceptors(mock(HandshakeInterceptor.class));
            };
        }
    }
}
