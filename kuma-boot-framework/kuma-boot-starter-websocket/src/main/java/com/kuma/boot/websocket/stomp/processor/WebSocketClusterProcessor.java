package com.kuma.boot.websocket.stomp.processor;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.websocket.stomp.autoconfigure.properties.WebSocketStompProperties;
import com.kuma.boot.websocket.stomp.core.IllegalChannelException;
import com.kuma.boot.websocket.stomp.core.PrincipalNotFoundException;
import com.kuma.boot.websocket.stomp.domain.WebSocketMessage;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.beans.factory.InitializingBean;

public class WebSocketClusterProcessor implements InitializingBean {
   private RedissonClient redissonClient;
   private WebSocketStompProperties webSocketStompProperties;
   private WebSocketMessageSender webSocketMessageSender;

   public WebSocketClusterProcessor() {
   }

   public void setRedissonClient(RedissonClient redissonClient) {
      this.redissonClient = redissonClient;
   }

   public void setWebSocketProperties(WebSocketStompProperties webSocketStompProperties) {
      this.webSocketStompProperties = webSocketStompProperties;
   }

   public void setWebSocketMessageSender(WebSocketMessageSender webSocketMessageSender) {
      this.webSocketMessageSender = webSocketMessageSender;
   }

   public void toClusterUser(WebSocketMessage<String> webSocketMessage) {
      try {
         this.webSocketMessageSender.toUser(webSocketMessage);
      } catch (PrincipalNotFoundException var4) {
         RTopic rTopic = this.redissonClient.getTopic(this.webSocketStompProperties.getTopic(), new JsonJacksonCodec());
         rTopic.publish(webSocketMessage);
         LogUtils.debug("Current instance can not found user [{}], publish message.", new Object[]{webSocketMessage.getTo()});
      } catch (IllegalChannelException var5) {
         LogUtils.error("Web socket channel is incorrect.", new Object[0]);
      }

   }

   @SuppressWarnings("unchecked")
   public void afterPropertiesSet() throws Exception {
      RTopic topic = this.redissonClient.getTopic(this.webSocketStompProperties.getTopic());
      topic.addListener(WebSocketMessage.class, (charSequence, webSocketMessage) -> {
         LogUtils.debug("Redisson received web socket sync message [{}]", new Object[]{webSocketMessage});
         this.webSocketMessageSender.toUser(webSocketMessage);
      });
   }
}
