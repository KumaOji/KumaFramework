package com.kuma.boot.websocket.stomp.processor;

import com.kuma.boot.websocket.stomp.core.IllegalChannelException;
import com.kuma.boot.websocket.stomp.core.PrincipalNotFoundException;
import com.kuma.boot.websocket.stomp.domain.WebSocketMessage;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;

public class WebSocketMessageSender {
   private static final Logger log = LoggerFactory.getLogger(WebSocketMessageSender.class);
   private SimpMessagingTemplate simpMessagingTemplate;
   private SimpUserRegistry simpUserRegistry;

   public WebSocketMessageSender() {
   }

   public void setSimpMessagingTemplate(SimpMessagingTemplate simpMessagingTemplate) {
      this.simpMessagingTemplate = simpMessagingTemplate;
   }

   public void setSimpUserRegistry(SimpUserRegistry simpUserRegistry) {
      this.simpUserRegistry = simpUserRegistry;
   }

   public <T> void toUser(WebSocketMessage<T> webSocketMessage) throws IllegalChannelException, PrincipalNotFoundException {
      SimpUser simpUser = this.simpUserRegistry.getUser(webSocketMessage.getTo());
      if (ObjectUtils.isEmpty(simpUser)) {
         throw new PrincipalNotFoundException("Web socket user principal is not found!");
      } else {
         log.debug("[Websocket] |- Web socket send message to user [{}].", webSocketMessage.getTo());
         this.simpMessagingTemplate.convertAndSendToUser(webSocketMessage.getTo(), webSocketMessage.getChannel(), webSocketMessage.getPayload());
      }
   }

   public <T> void toAll(String channel, T payload) {
      this.simpMessagingTemplate.convertAndSend(channel, payload);
   }

   public <T> void sendNoticeToAll(T payload) {
      this.toAll("/broadcast/notice", payload);
   }

   public <T> void sendOnlineToAll(T payload) {
      this.toAll("/broadcast/online", payload);
   }
}
