package com.kuma.boot.websocket.spring.common.distribute;

import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

public class RedisMessageListenerInitializer {
   private final RedisMessageListenerContainer redisMessageListenerContainer;
   private final RedisMessageDistributor redisWebsocketMessageListener;

   public RedisMessageListenerInitializer(RedisMessageListenerContainer redisMessageListenerContainer, RedisMessageDistributor redisWebsocketMessageListener) {
      this.redisMessageListenerContainer = redisMessageListenerContainer;
      this.redisWebsocketMessageListener = redisWebsocketMessageListener;
   }

   @PostConstruct
   public void addMessageListener() {
      this.redisMessageListenerContainer.addMessageListener(this.redisWebsocketMessageListener, new PatternTopic("websocket-send"));
   }
}
