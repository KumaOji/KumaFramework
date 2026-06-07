package com.kuma.cloud.blog.websocket;

import com.kuma.boot.common.utils.json.JacksonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 聊天室在线用户注册表（Redis 版）。
 *
 * <p>数据结构：
 * <ul>
 *   <li>每个房间一个 Hash：{@code chat:room:online:{roomId}}，field=sessionId，value=ChatUserInfo(JSON)。
 *       人数 = HLEN，在线列表 = HVALS。Hash 最后一个 field 被删后 Redis 自动回收该 key。</li>
 *   <li>每个会话一个 Set：{@code chat:session:rooms:{sessionId}}，member=roomId，
 *       用于断线时反查该会话加入过的房间。</li>
 * </ul>
 *
 * <p>相比内存版的收益：多实例共享同一份在线状态、单实例重启后由其他实例维持的数据不丢。
 *
 * <p>已知限制：若某实例进程崩溃（未触发 disconnect 事件），其持有的 session 条目会在 Redis 中残留，
 * 直到对应客户端重连产生新 session。如需彻底规避，可在此基础上加 TTL + STOMP 心跳续期。
 */
@Component
@RequiredArgsConstructor
public class OnlineUserRegistry {

    private static final String ROOM_ONLINE_PREFIX = "chat:room:online:";
    private static final String SESSION_ROOMS_PREFIX = "chat:session:rooms:";

    private final StringRedisTemplate redis;

    private String roomKey(Long roomId) {
        return ROOM_ONLINE_PREFIX + roomId;
    }

    private String sessionKey(String sessionId) {
        return SESSION_ROOMS_PREFIX + sessionId;
    }

    public void join(Long roomId, ChatUserInfo userInfo) {
        String json = JacksonUtils.toJSONString(userInfo);
        redis.<String, String>opsForHash().put(roomKey(roomId), userInfo.getSessionId(), json);
        redis.opsForSet().add(sessionKey(userInfo.getSessionId()), String.valueOf(roomId));
    }

    public void leave(Long roomId, String sessionId) {
        redis.opsForHash().delete(roomKey(roomId), sessionId);
        redis.opsForSet().remove(sessionKey(sessionId), String.valueOf(roomId));
    }

    /** WebSocket 会话断开时调用；返回该会话当时所在的房间及用户信息，供广播 LEAVE。 */
    public Map<Long, ChatUserInfo> disconnect(String sessionId) {
        Map<Long, ChatUserInfo> result = new HashMap<>();
        Set<String> rooms = redis.opsForSet().members(sessionKey(sessionId));
        redis.delete(sessionKey(sessionId));
        if (rooms == null || rooms.isEmpty()) return result;

        HashOperations<String, String, String> hash = redis.opsForHash();
        for (String r : rooms) {
            Long roomId = Long.valueOf(r);
            String json = hash.get(roomKey(roomId), sessionId);
            hash.delete(roomKey(roomId), sessionId);
            if (json != null) {
                ChatUserInfo info = JacksonUtils.readValue(json, ChatUserInfo.class);
                if (info != null) result.put(roomId, info);
            }
        }
        return result;
    }

    public List<ChatUserInfo> getOnlineUsers(Long roomId) {
        List<String> values = redis.<String, String>opsForHash().values(roomKey(roomId));
        if (values == null || values.isEmpty()) return List.of();
        List<ChatUserInfo> users = new ArrayList<>(values.size());
        for (String json : values) {
            ChatUserInfo info = JacksonUtils.readValue(json, ChatUserInfo.class);
            if (info != null) users.add(info);
        }
        return users;
    }

    public int getOnlineCount(Long roomId) {
        Long size = redis.opsForHash().size(roomKey(roomId));
        return size == null ? 0 : size.intValue();
    }
}
