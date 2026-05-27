package com.kuma.cloud.blog.websocket;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlineUserRegistry {

    /** roomId -> (sessionId -> userInfo) */
    private final ConcurrentHashMap<Long, ConcurrentHashMap<String, ChatUserInfo>> roomMap = new ConcurrentHashMap<>();

    /** sessionId -> roomIds */
    private final ConcurrentHashMap<String, Set<Long>> sessionMap = new ConcurrentHashMap<>();

    public void join(Long roomId, ChatUserInfo userInfo) {
        roomMap.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
               .put(userInfo.getSessionId(), userInfo);
        sessionMap.computeIfAbsent(userInfo.getSessionId(), k -> ConcurrentHashMap.newKeySet())
                  .add(roomId);
    }

    public void leave(Long roomId, String sessionId) {
        ConcurrentHashMap<String, ChatUserInfo> sessions = roomMap.get(roomId);
        if (sessions != null) sessions.remove(sessionId);
        Set<Long> rooms = sessionMap.get(sessionId);
        if (rooms != null) rooms.remove(roomId);
    }

    /** Called when WebSocket session disconnects; returns rooms the user was in. */
    public Map<Long, ChatUserInfo> disconnect(String sessionId) {
        Set<Long> rooms = sessionMap.remove(sessionId);
        Map<Long, ChatUserInfo> result = new HashMap<>();
        if (rooms == null) return result;
        for (Long roomId : rooms) {
            ConcurrentHashMap<String, ChatUserInfo> sessions = roomMap.get(roomId);
            if (sessions != null) {
                ChatUserInfo info = sessions.remove(sessionId);
                if (info != null) result.put(roomId, info);
            }
        }
        return result;
    }

    public List<ChatUserInfo> getOnlineUsers(Long roomId) {
        ConcurrentHashMap<String, ChatUserInfo> sessions = roomMap.get(roomId);
        if (sessions == null) return List.of();
        return new ArrayList<>(sessions.values());
    }

    public int getOnlineCount(Long roomId) {
        ConcurrentHashMap<String, ChatUserInfo> sessions = roomMap.get(roomId);
        return sessions == null ? 0 : sessions.size();
    }
}
