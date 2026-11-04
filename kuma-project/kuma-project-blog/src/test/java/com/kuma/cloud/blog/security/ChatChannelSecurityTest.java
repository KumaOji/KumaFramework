package com.kuma.cloud.blog.security;

import com.kuma.cloud.blog.domain.vo.LoginVO;
import com.kuma.cloud.blog.domain.vo.UserAuthoritiesVO;
import com.kuma.cloud.blog.service.ChatBlacklistService;
import com.kuma.cloud.blog.service.PermissionService;
import com.kuma.cloud.blog.websocket.ChatAccessService;
import com.kuma.cloud.blog.websocket.ChatChannelInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatChannelSecurityTest {
    private final PermissionService permissions = mock(PermissionService.class);
    private final ChatBlacklistService blacklist = mock(ChatBlacklistService.class);
    private final JwtDecoder decoder = mock(JwtDecoder.class);
    private final BlogJwtAuthenticationConverter converter = mock(BlogJwtAuthenticationConverter.class);
    private final ChatChannelInterceptor interceptor = new ChatChannelInterceptor(decoder, converter,
            new ChatAccessService(permissions, blacklist));

    @BeforeEach
    void login() {
        var login = new LoginVO();
        login.setUserId(1L);
        login.setUsername("reader");
        login.setEmail("reader@example.com");
        var jwt = Jwt.withTokenValue("token").header("alg", "RS256").subject("reader").build();
        when(decoder.decode("token")).thenReturn(jwt);
        when(converter.convert(jwt)).thenReturn(new UsernamePasswordAuthenticationToken(new BlogUserDetails(login), null));
        grant(BlogPermissions.CHAT_READ);
    }

    @Test
    void readOnlyUserCanSubscribeButCannotSendAndGrantChangesApplyToExistingSession() {
        assertThat(interceptor.preSend(frame(StompCommand.SUBSCRIBE, "/topic/chat.1"), null)).isNotNull();
        assertThatThrownBy(() -> interceptor.preSend(frame(StompCommand.SEND, "/app/chat.send/1"), null))
                .isInstanceOf(AccessDeniedException.class);
        grant(BlogPermissions.CHAT_READ, BlogPermissions.CHAT_SEND);
        assertThat(interceptor.preSend(frame(StompCommand.SEND, "/app/chat.send/1"), null)).isNotNull();
        grant();
        assertThatThrownBy(() -> interceptor.preSend(frame(StompCommand.SEND, "/app/chat.send/1"), null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void forbidsBrokerInjectionWildcardSubscriptionsAndUnknownDestinations() {
        grant(BlogPermissions.CHAT_ALL);
        for (String destination : List.of("/topic/chat.1", "/app/other", "/app/chat.send/0")) {
            assertThatThrownBy(() -> interceptor.preSend(frame(StompCommand.SEND, destination), null))
                    .isInstanceOf(AccessDeniedException.class);
        }
        for (String destination : List.of("/topic/**", "/topic/chat.*", "/topic/other")) {
            assertThatThrownBy(() -> interceptor.preSend(frame(StompCommand.SUBSCRIBE, destination), null))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Test
    void stopsExistingSubscriptionAfterRevocationLogoutOrBlacklist() {
        interceptor.preSend(frame(StompCommand.CONNECT, null), null);
        var headers = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headers.setSessionId("session");
        var broadcast = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
        assertThat(interceptor.filterOutbound(broadcast)).isSameAs(broadcast);
        grant();
        assertThat(interceptor.filterOutbound(broadcast)).isNull();
        grant(BlogPermissions.CHAT_ALL);
        when(blacklist.isBlocked("reader@example.com")).thenReturn(true);
        assertThat(interceptor.filterOutbound(broadcast)).isNull();
        when(blacklist.isBlocked("reader@example.com")).thenReturn(false);
        when(decoder.decode("token")).thenThrow(new IllegalArgumentException("expired or revoked"));
        assertThat(interceptor.filterOutbound(broadcast)).isNull();
        assertThatThrownBy(() -> interceptor.preSend(frame(StompCommand.SEND, "/app/chat.send/1"), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void grant(String... codes) {
        var authorities = new UserAuthoritiesVO();
        authorities.setDirectPermissions(List.of(codes));
        when(permissions.getUserAuthorities(1L)).thenReturn(authorities);
    }

    private Message<byte[]> frame(StompCommand command, String destination) {
        var headers = StompHeaderAccessor.create(command);
        headers.setSessionId("session");
        headers.setSessionAttributes(new HashMap<>(Map.of("chatAccessToken", "token")));
        headers.setDestination(destination);
        return MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
    }
}
