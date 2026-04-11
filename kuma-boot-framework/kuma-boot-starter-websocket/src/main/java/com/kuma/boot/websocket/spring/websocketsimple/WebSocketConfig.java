package com.kuma.boot.websocket.spring.websocketsimple;

import cn.hutool.core.collection.CollUtil;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

@ConditionalOnProperty(
   name = {"zebra.web.websocket.enabled"},
   havingValue = "true"
)
@Configuration
@EnableWebSocket
public class WebSocketConfig {
   public WebSocketConfig() {
   }

   @Bean
   public WebSocketConfigurer webSocketConfigurer(@Autowired WebSocketEmitterService webSocketEmitterService, @Autowired(required = false) TokenHandler tokenHandler, @Autowired(required = false) List<? extends WebSocketMessageListener<?>> messageListeners) {
      if (tokenHandler == null) {
         tokenHandler = new DefaultTokenHandler();
      }

      if (CollUtil.isEmpty(messageListeners)) {
         messageListeners = new ArrayList();
      }

      WebSocketSessionHandlerDecorator webSocketSessionHandlerDecorator = new WebSocketSessionHandlerDecorator(new JsonWebSocketMessageHandler(messageListeners), webSocketEmitterService);
      return (registry) -> registry.addHandler(webSocketSessionHandlerDecorator, new String[]{"/websocket"}).addInterceptors(new HandshakeInterceptor[]{new LoginInfoHandshakeInterceptor(tokenHandler)}).setAllowedOriginPatterns(new String[]{"*"});
   }

   @Bean
   public WebSocketEmitterService webSocketEmitterService() {
      return new WebSocketEmitterService();
   }
}
