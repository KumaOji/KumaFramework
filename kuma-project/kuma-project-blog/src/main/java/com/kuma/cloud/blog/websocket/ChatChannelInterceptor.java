package com.kuma.cloud.blog.websocket;

import com.kuma.cloud.blog.security.BlogJwtAuthenticationConverter;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.security.BlogUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Validate identity and current grants for every client operation; never accept broker writes. */
@RequiredArgsConstructor
public class ChatChannelInterceptor implements ChannelInterceptor, ApplicationListener<SessionDisconnectEvent> {
    private final JwtDecoder decoder;
    private final BlogJwtAuthenticationConverter converter;
    private final ChatAccessService access;
    private final Map<String, String> sessionTokens = new ConcurrentHashMap<>();

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        sessionTokens.remove(event.getSessionId());
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headers = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (headers == null) throw new AccessDeniedException("无效的聊天消息");
        StompCommand command = headers.getCommand();
        if (command == StompCommand.DISCONNECT) {
            sessionTokens.remove(headers.getSessionId());
            return message;
        }
        if (command == null || command == StompCommand.UNSUBSCRIBE) return message;
        var attributes = headers.getSessionAttributes();
        if (attributes == null || !(attributes.get("chatAccessToken") instanceof String token)) {
            throw new AccessDeniedException("请先登录后再使用聊天");
        }
        var authentication = converter.convert(decoder.decode(token));
        var login = ((BlogUserDetails) authentication.getPrincipal()).getLoginResponse();
        access.require(login, BlogPermissions.CHAT_READ);
        sessionTokens.put(headers.getSessionId(), token);
        attributes.put("loginResponse", login);
        String destination = headers.getDestination();
        if (command == StompCommand.SUBSCRIBE) {
            if (destination == null || !destination.matches("/topic/chat\\.[1-9][0-9]*(\\.online)?")) {
                throw new AccessDeniedException("不允许订阅此聊天主题");
            }
        } else if (command == StompCommand.SEND) {
            if (destination == null || !destination.matches("/app/chat\\.(join|profile|send|leave)/[1-9][0-9]*")) {
                throw new AccessDeniedException("不允许发送到此聊天地址");
            }
            if (destination.startsWith("/app/chat.send/")) access.require(login, BlogPermissions.CHAT_SEND);
        } else if (command != StompCommand.CONNECT && command != StompCommand.STOMP) {
            throw new AccessDeniedException("不允许的聊天操作");
        }
        return message;
    }

    /** Existing subscriptions must also stop receiving after logout, expiry or grant revocation. */
    public Message<?> filterOutbound(Message<?> message) {
        if (SimpMessageHeaderAccessor.getMessageType(message.getHeaders()) != SimpMessageType.MESSAGE) return message;
        String sessionId = SimpMessageHeaderAccessor.getSessionId(message.getHeaders());
        String token = sessionId == null ? null : sessionTokens.get(sessionId);
        if (token == null) return null;
        try {
            var authentication = converter.convert(decoder.decode(token));
            access.require(((BlogUserDetails) authentication.getPrincipal()).getLoginResponse(), BlogPermissions.CHAT_READ);
            return message;
        } catch (RuntimeException denied) {
            return null;
        }
    }
}
