package com.kuma.boot.websocket.spring.common.distribute;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.websocket.spring.common.session.WebSocketSessionStore;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

public class RedisMessageDistributor extends AbstractMessageDistributor implements MessageListener {
   public static final String CHANNEL = "websocket-send";
   private final StringRedisTemplate stringRedisTemplate;

   public RedisMessageDistributor(WebSocketSessionStore webSocketSessionStore, StringRedisTemplate stringRedisTemplate) {
      super(webSocketSessionStore);
      this.stringRedisTemplate = stringRedisTemplate;
   }

   public void distribute(MessageDO messageDO) {
      String str = JacksonUtils.toJson(messageDO);
      this.stringRedisTemplate.convertAndSend("websocket-send", str);
   }

   public void onMessage(Message message, byte[] bytes) {
      LogUtils.info("redis channel Listener message send {}", new Object[]{message});
      byte[] channelBytes = message.getChannel();
      RedisSerializer<String> stringSerializer = this.stringRedisTemplate.getStringSerializer();
      String channel = (String)stringSerializer.deserialize(channelBytes);
      if ("websocket-send".equals(channel)) {
         byte[] bodyBytes = message.getBody();
         String body = (String)stringSerializer.deserialize(bodyBytes);
         MessageDO messageDO = (MessageDO)JacksonUtils.toObject(body, MessageDO.class);
         this.doSend(messageDO);
      }

   }
}
