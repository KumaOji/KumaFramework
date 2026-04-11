package com.kuma.boot.websocket.spring.autoconfigure.config;

import com.kuma.boot.websocket.spring.autoconfigure.properties.WebSocketSpringProperties;
import com.kuma.boot.websocket.spring.common.handler.CustomWebSocketHandler;
import com.kuma.boot.websocket.spring.common.handler.PlanTextMessageHandler;
import com.kuma.boot.websocket.spring.common.session.DefaultWebSocketSessionStore;
import com.kuma.boot.websocket.spring.common.session.MapSessionWebSocketHandlerDecorator;
import com.kuma.boot.websocket.spring.common.session.SessionKeyGenerator;
import com.kuma.boot.websocket.spring.common.session.WebSocketSessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;

public class WebSocketHandlerConfig {
   private final WebSocketSpringProperties webSocketSpringProperties;

   public WebSocketHandlerConfig(WebSocketSpringProperties webSocketSpringProperties) {
      this.webSocketSpringProperties = webSocketSpringProperties;
   }

   @Bean
   @ConditionalOnMissingBean
   public WebSocketSessionStore webSocketSessionStore(@Autowired(required = false) SessionKeyGenerator sessionKeyGenerator) {
      return new DefaultWebSocketSessionStore(sessionKeyGenerator);
   }

   @Bean
   @ConditionalOnMissingBean({WebSocketHandler.class})
   public WebSocketHandler webSocketHandler(WebSocketSessionStore webSocketSessionStore, @Autowired(required = false) PlanTextMessageHandler planTextMessageHandler) {
      CustomWebSocketHandler customWebSocketHandler = new CustomWebSocketHandler(planTextMessageHandler);
      return (WebSocketHandler)(this.webSocketSpringProperties.isMapSession() ? new MapSessionWebSocketHandlerDecorator(customWebSocketHandler, webSocketSessionStore, this.webSocketSpringProperties.getConcurrent()) : customWebSocketHandler);
   }
}
