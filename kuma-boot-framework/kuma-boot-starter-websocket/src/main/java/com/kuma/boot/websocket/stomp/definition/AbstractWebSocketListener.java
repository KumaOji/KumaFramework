package com.kuma.boot.websocket.stomp.definition;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.websocket.stomp.processor.WebSocketMessageSender;
import com.kuma.boot.websocket.stomp.utils.WebSocketUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public abstract class AbstractWebSocketListener<E extends ApplicationEvent> implements ApplicationListener<E> {
   protected final WebSocketMessageSender webSocketMessageSender;
   protected final RedisRepository redisRepository;

   public AbstractWebSocketListener(WebSocketMessageSender webSocketMessageSender, RedisRepository redisRepository) {
      this.webSocketMessageSender = webSocketMessageSender;
      this.redisRepository = redisRepository;
   }

   private int getOnlineCount() {
      Long count = this.redisRepository.bitCount("data:msg:online:user");
      return count.intValue();
   }

   protected void syncUserCountToAll() {
      int count = WebSocketUtils.getOnlineCount();
      this.webSocketMessageSender.sendOnlineToAll(count);
   }
}
