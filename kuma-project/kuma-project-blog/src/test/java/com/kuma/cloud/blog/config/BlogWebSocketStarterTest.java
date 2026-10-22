package com.kuma.cloud.blog.config;

import com.kuma.boot.websocket.stomp.autoconfigure.MessageWebSocketAutoConfiguration;
import com.kuma.boot.websocket.stomp.autoconfigure.properties.WebSocketStompProperties;
import com.kuma.cloud.blog.domain.vo.LoginVO;
import com.kuma.cloud.blog.security.*;
import com.kuma.cloud.blog.service.ChatBlacklistService;
import com.kuma.cloud.blog.websocket.ChatHandshakeInterceptor;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.mock.web.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

class BlogWebSocketStarterTest {
    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withInitializer(context -> {
                try {
                    context.getEnvironment().getPropertySources().addLast(
                            new org.springframework.core.io.support.ResourcePropertySource("classpath:blog-websocket.properties"));
                } catch (java.io.IOException error) {
                    throw new java.io.UncheckedIOException(error);
                }
            })
            .withConfiguration(AutoConfigurations.of(MessageWebSocketAutoConfiguration.class))
            .withUserConfiguration(WebSocketConfig.class, Dependencies.class);

    @Test
    void blogDefaultsExposeExistingSockJsEndpointAndAnonymousTopicSubscriptions() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            var properties = context.getBean(WebSocketStompProperties.class);
            assertThat(properties.getEndpoint()).isEqualTo("/ws/chat");
            assertThat(properties.getBrokerPrefixes()).containsExactly("/topic");
            assertThat(properties.getApplicationPrefixes()).containsExactly("/app");
            var mapping = context.getBean("stompWebSocketHandlerMapping", SimpleUrlHandlerMapping.class);
            assertThat(mapping.getUrlMap()).containsKey("/ws/chat/**");
            webAppContextSetup(context.getSourceApplicationContext()).build()
                    .perform(get("/ws/chat/info")).andExpect(status().isOk());

            var outbound = context.getBean("clientOutboundChannel", SubscribableChannel.class);
            var inbound = context.getBean("clientInboundChannel", MessageChannel.class);
            var broker = context.getBean(SimpleBrokerMessageHandler.class);
            var received = new LinkedBlockingQueue<Message<?>>();
            MessageHandler receiver = received::add;
            outbound.subscribe(receiver);
            try {
                inbound.send(frame(StompCommand.CONNECT, null));
                assertThat(received.poll(3, TimeUnit.SECONDS)).isNotNull();
                inbound.send(frame(StompCommand.SUBSCRIBE, "/topic/chat.1"));
                Message<?> probe = frame(StompCommand.SEND, "/topic/chat.1");
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);
                while (!broker.getSubscriptionRegistry().findSubscriptions(probe).containsKey("guest-session")
                        && System.nanoTime() < deadline) Thread.sleep(10);
                assertThat(broker.getSubscriptionRegistry().findSubscriptions(probe)).containsKey("guest-session");
                var payload = new com.kuma.cloud.blog.domain.vo.ChatMessageVO();
                payload.setRoomId(1L);
                payload.setContent("chat-message");
                payload.setTimestamp(java.time.LocalDateTime.now());
                context.getBean(SimpMessagingTemplate.class).convertAndSend("/topic/chat.1", payload);
                Message<?> broadcast = received.poll(3, TimeUnit.SECONDS);
                assertThat(broadcast).isNotNull();
                assertThat(new String((byte[]) broadcast.getPayload(), java.nio.charset.StandardCharsets.UTF_8))
                        .contains("\"content\":\"chat-message\"", "\"roomId\":1");
            } finally {
                outbound.unsubscribe(receiver);
            }
        });
    }

    @Test
    void handshakeKeepsCookieIdentityBlacklistAndGuestFallback() {
        runner.run(context -> {
            var interceptor = context.getBean(ChatHandshakeInterceptor.class);
            var decoder = context.getBean(JwtDecoder.class);
            var converter = context.getBean(BlogJwtAuthenticationConverter.class);
            var blacklist = context.getBean(ChatBlacklistService.class);
            var login = new LoginVO();
            login.setEmail("user@example.com");
            var jwt = Jwt.withTokenValue("valid-jwt").header("alg", "RS256").subject("user").build();
            when(decoder.decode("valid-jwt")).thenReturn(jwt);
            when(converter.convert(jwt)).thenReturn(new UsernamePasswordAuthenticationToken(
                    new BlogUserDetails(login), null));
            var attributes = new HashMap<String, Object>();
            assertThat(handshake(interceptor, "valid-jwt", attributes)).isTrue();
            assertThat(attributes).containsEntry("loginResponse", login);

            when(blacklist.isBlocked(login.getEmail())).thenReturn(true);
            assertThat(handshake(interceptor, "valid-jwt", new HashMap<>())).isFalse();
            attributes.clear();
            assertThat(handshake(interceptor, null, attributes)).isTrue();
            assertThat(attributes).isEmpty();
            when(decoder.decode("invalid-jwt")).thenThrow(new IllegalArgumentException("invalid"));
            assertThat(handshake(interceptor, "invalid-jwt", attributes)).isTrue();
            assertThat(attributes).isEmpty();
        });
    }

    private static boolean handshake(ChatHandshakeInterceptor interceptor, String token, Map<String, Object> attributes) {
        var request = new MockHttpServletRequest();
        if (token != null) request.setCookies(new Cookie(OAuth2CookieService.ACCESS_TOKEN_COOKIE, token));
        return interceptor.beforeHandshake(new ServletServerHttpRequest(request),
                new ServletServerHttpResponse(new MockHttpServletResponse()), mock(WebSocketHandler.class), attributes);
    }

    private static Message<byte[]> frame(StompCommand command, String destination) {
        var headers = StompHeaderAccessor.create(command);
        headers.setSessionId("guest-session");
        headers.setSessionAttributes(new HashMap<>());
        if (destination != null) headers.setDestination(destination);
        if (command == StompCommand.SUBSCRIBE) headers.setSubscriptionId("chat-subscription");
        return MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
    }

    @Configuration(proxyBeanMethods = false)
    @EnableWebMvc
    static class Dependencies {
        @Bean JwtDecoder jwtDecoder() { return mock(JwtDecoder.class); }
        @Bean BlogJwtAuthenticationConverter converter() { return mock(BlogJwtAuthenticationConverter.class); }
        @Bean ChatBlacklistService blacklist() { return mock(ChatBlacklistService.class); }
    }
}
