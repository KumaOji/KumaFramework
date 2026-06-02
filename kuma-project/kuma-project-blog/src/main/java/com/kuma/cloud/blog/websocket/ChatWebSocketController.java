package com.kuma.cloud.blog.websocket;

import com.kuma.cloud.blog.domain.entity.ChatHistory;
import com.kuma.cloud.blog.domain.vo.ChatMessageVO;
import com.kuma.cloud.blog.domain.vo.ChatSendRequest;
import com.kuma.cloud.blog.domain.vo.LoginResponse;
import com.kuma.cloud.blog.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final OnlineUserRegistry registry;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat.join/{roomId}")
    public void join(@DestinationVariable Long roomId, SimpMessageHeaderAccessor accessor) {
        ChatUserInfo userInfo = resolveUser(accessor);
        registry.join(roomId, userInfo);

        ChatMessageVO msg = buildMessage("JOIN", roomId, userInfo, userInfo.getNickname() + " 加入了聊天室");
        broadcast(roomId, msg);
    }

    @MessageMapping("/chat.send/{roomId}")
    public void send(@DestinationVariable Long roomId, ChatSendRequest request,
                     SimpMessageHeaderAccessor accessor) {
        if (request.getContent() == null || request.getContent().isBlank()) return;
        String content = request.getContent().strip();
        if (content.length() > 500) content = content.substring(0, 500);

        ChatUserInfo userInfo = resolveUser(accessor);

        ChatHistory history = new ChatHistory();
        history.setRoomId(roomId);
        history.setUserId(userInfo.getUserId());
        history.setNickname(userInfo.getNickname());
        history.setAvatar(userInfo.getAvatar());
        history.setContent(content);
        history.setMessageType("CHAT");
        // Persist asynchronously — don't block the broadcast path
        CompletableFuture.runAsync(() -> chatRoomService.saveHistory(history));

        ChatMessageVO msg = buildMessage("CHAT", roomId, userInfo, content);
        broadcast(roomId, msg);
    }

    @MessageMapping("/chat.leave/{roomId}")
    public void leave(@DestinationVariable Long roomId, SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        registry.leave(roomId, sessionId);

        ChatUserInfo userInfo = resolveUser(accessor);
        ChatMessageVO msg = buildMessage("LEAVE", roomId, userInfo, userInfo.getNickname() + " 离开了聊天室");
        broadcast(roomId, msg);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        Map<Long, ChatUserInfo> leftRooms = registry.disconnect(sessionId);
        leftRooms.forEach((roomId, userInfo) -> {
            ChatMessageVO msg = buildMessage("LEAVE", roomId, userInfo, userInfo.getNickname() + " 离开了聊天室");
            broadcast(roomId, msg);
        });
    }

    private ChatUserInfo resolveUser(SimpMessageHeaderAccessor accessor) {
        Map<String, Object> attrs = accessor.getSessionAttributes();
        String sessionId = accessor.getSessionId();
        LoginResponse lr = attrs != null ? (LoginResponse) attrs.get("loginResponse") : null;
        if (lr != null) {
            String nickname = lr.getNickname() != null ? lr.getNickname() : lr.getUsername();
            return new ChatUserInfo(lr.getUserId(), nickname, null, sessionId);
        }
        String guestNick = "访客" + sessionId.substring(0, Math.min(4, sessionId.length()));
        return new ChatUserInfo(null, guestNick, null, sessionId);
    }

    private ChatMessageVO buildMessage(String type, Long roomId, ChatUserInfo user, String content) {
        ChatMessageVO msg = new ChatMessageVO();
        msg.setType(type);
        msg.setRoomId(roomId);
        msg.setUserId(user.getUserId());
        msg.setNickname(user.getNickname());
        msg.setAvatar(user.getAvatar());
        msg.setContent(content);
        msg.setTimestamp(LocalDateTime.now());
        return msg;
    }

    private void broadcast(Long roomId, ChatMessageVO msg) {
        messagingTemplate.convertAndSend("/topic/chat." + roomId, msg);
    }
}
