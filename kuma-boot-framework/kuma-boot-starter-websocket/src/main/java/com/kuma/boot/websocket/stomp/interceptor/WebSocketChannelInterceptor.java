package com.kuma.boot.websocket.stomp.interceptor;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.websocket.stomp.core.MessageConstants;
import com.kuma.boot.websocket.stomp.domain.WebSocketPrincipal;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

public class WebSocketChannelInterceptor implements ChannelInterceptor {
   private static final Logger log = LoggerFactory.getLogger(WebSocketChannelInterceptor.class);

   public WebSocketChannelInterceptor() {
   }

   public Message<?> preSend(Message<?> message, MessageChannel channel) {
      StompHeaderAccessor accessor = (StompHeaderAccessor)MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
      WebSocketPrincipal principal = (WebSocketPrincipal)accessor.getUser();
      if (ObjectUtils.isEmpty(principal)) {
         log.warn("[Websocket] |- WebSocket channel cannot fetch user principal.");
         return null;
      } else {
         StompCommand command = accessor.getCommand();
         if (ObjectUtils.isNotEmpty(command)) {
            switch (command) {
               case CONNECT:
                  List<String> tokenHeaders = accessor.getNativeHeader("Authorization");
                  String token = null;
                  if (CollectionUtils.isNotEmpty(tokenHeaders)) {
                     String temp = (String)tokenHeaders.get(0);
                     if (StringUtils.isNotBlank(temp) && StringUtils.startWith(temp, MessageConstants.BEARER_TOKEN)) {
                        token = StringUtils.removePrefix(temp, MessageConstants.BEARER_TOKEN);
                     }
                  }

                  WebSocketPrincipal user = (WebSocketPrincipal)accessor.getUser();
                  log.debug("[Websocket] |- Authentication user [{}] transmit token [{}] from frontend.", user.getName(), token);
               case DISCONNECT:
            }
         }

         return message;
      }
   }

   public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
   }

   public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
   }

   public boolean preReceive(MessageChannel channel) {
      return true;
   }

   public Message<?> postReceive(Message<?> message, MessageChannel channel) {
      return message;
   }

   public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
   }
}
