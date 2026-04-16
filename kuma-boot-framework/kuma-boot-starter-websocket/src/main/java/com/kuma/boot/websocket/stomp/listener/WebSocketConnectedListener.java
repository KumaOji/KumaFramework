package com.kuma.boot.websocket.stomp.listener;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.websocket.stomp.definition.AbstractWebSocketListener;
import com.kuma.boot.websocket.stomp.domain.WebSocketPrincipal;
import com.kuma.boot.websocket.stomp.processor.WebSocketMessageSender;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
@ConditionalOnProperty(prefix = "kuma.boot.websocket.stomp", name = "enabled", havingValue = "true")
public class WebSocketConnectedListener extends AbstractWebSocketListener<SessionConnectedEvent> {
   private static final Logger log = LoggerFactory.getLogger(WebSocketConnectedListener.class);

   public WebSocketConnectedListener(WebSocketMessageSender webSocketMessageSender, RedisRepository redisRepository) {
      super(webSocketMessageSender, redisRepository);
   }

   public void onApplicationEvent(SessionConnectedEvent event) {
      WebSocketPrincipal principal = (WebSocketPrincipal)event.getUser();
      if (ObjectUtils.isNotEmpty(principal)) {
         this.redisRepository.setBit("data:msg:online:user", principal.getName(), true);
         log.debug("[Websocket] |- WebSocket user [{}] Offline.", principal);
         this.syncUserCountToAll();
      }

   }
}
