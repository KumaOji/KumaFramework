package com.kuma.cloud.blog.websocket;

import com.kuma.cloud.blog.domain.entity.ChatHistory;
import com.kuma.cloud.blog.domain.dto.ChatGuestProfileDTO;
import com.kuma.cloud.blog.domain.vo.ChatMessageVO;
import com.kuma.cloud.blog.domain.dto.ChatSendDTO;
import com.kuma.cloud.blog.domain.vo.LoginVO;
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

    private static final String GUEST_NICK_KEY = "guestNickname";
    private static final String GUEST_AVATAR_KEY = "guestAvatar";

    @MessageMapping("/chat.join/{roomId}")
    public void join(@DestinationVariable Long roomId, ChatGuestProfileDTO profile,
                     SimpMessageHeaderAccessor accessor) {
        ChatUserInfo userInfo = resolveUser(accessor, profile);
        registry.join(roomId, userInfo);

        ChatMessageVO msg = buildMessage("JOIN", roomId, userInfo, userInfo.getNickname() + " 加入了聊天室");
        broadcast(roomId, msg);
        broadcastOnlineCount(roomId);
    }

    /** 静默更新访客资料（不广播系统消息） */
    @MessageMapping("/chat.profile/{roomId}")
    public void updateProfile(@DestinationVariable Long roomId, ChatGuestProfileDTO profile,
                              SimpMessageHeaderAccessor accessor) {
        ChatUserInfo userInfo = resolveUser(accessor, profile);
        registry.join(roomId, userInfo);
    }

    @MessageMapping("/chat.send/{roomId}")
    public void send(@DestinationVariable Long roomId, ChatSendDTO request,
                     SimpMessageHeaderAccessor accessor) {
        if (request.getContent() == null || request.getContent().isBlank()) return;
        String content = request.getContent().strip();
        if (content.length() > 500) content = content.substring(0, 500);

        ChatGuestProfileDTO profile = new ChatGuestProfileDTO();
        profile.setNickname(request.getNickname());
        profile.setAvatar(request.getAvatar());
        ChatUserInfo userInfo = resolveUser(accessor, profile);

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
        broadcastOnlineCount(roomId);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        Map<Long, ChatUserInfo> leftRooms = registry.disconnect(sessionId);
        leftRooms.forEach((roomId, userInfo) -> {
            ChatMessageVO msg = buildMessage("LEAVE", roomId, userInfo, userInfo.getNickname() + " 离开了聊天室");
            broadcast(roomId, msg);
            broadcastOnlineCount(roomId);
        });
    }

    private void storeGuestProfile(SimpMessageHeaderAccessor accessor, ChatGuestProfileDTO profile) {
        if (profile == null) return;
        Map<String, Object> attrs = accessor.getSessionAttributes();
        if (attrs == null) return;
        if (profile.getNickname() != null && !profile.getNickname().isBlank()) {
            String nick = profile.getNickname().strip();
            if (nick.length() > 50) nick = nick.substring(0, 50);
            attrs.put(GUEST_NICK_KEY, nick);
        }
        if (profile.getAvatar() != null && !profile.getAvatar().isBlank()) {
            String av = profile.getAvatar().strip();
            if (av.length() > 255) av = av.substring(0, 255);
            attrs.put(GUEST_AVATAR_KEY, av);
        }
    }

    private ChatUserInfo resolveUser(SimpMessageHeaderAccessor accessor, ChatGuestProfileDTO profile) {
        storeGuestProfile(accessor, profile);
        return resolveUser(accessor);
    }

    private ChatUserInfo resolveUser(SimpMessageHeaderAccessor accessor) {
        Map<String, Object> attrs = accessor.getSessionAttributes();
        String sessionId = accessor.getSessionId();
        LoginVO lr = attrs != null ? (LoginVO) attrs.get("loginResponse") : null;
        if (lr != null) {
            String nickname = lr.getNickname() != null ? lr.getNickname() : lr.getUsername();
            return new ChatUserInfo(lr.getUserId(), nickname, null, sessionId);
        }
        String guestNick = attrs != null ? (String) attrs.get(GUEST_NICK_KEY) : null;
        String guestAvatar = attrs != null ? (String) attrs.get(GUEST_AVATAR_KEY) : null;
        if (guestNick == null || guestNick.isBlank()) {
            guestNick = "访客" + sessionId.substring(0, Math.min(4, sessionId.length()));
        }
        return new ChatUserInfo(null, guestNick, guestAvatar, sessionId);
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

    /** 广播房间最新在线人数到 /topic/chat.{roomId}.online，payload: {roomId, onlineCount} */
    private void broadcastOnlineCount(Long roomId) {
        int count = registry.getOnlineCount(roomId);
        Object payload = Map.of("roomId", roomId, "onlineCount", count);
        messagingTemplate.convertAndSend("/topic/chat." + roomId + ".online", payload);
    }
}
