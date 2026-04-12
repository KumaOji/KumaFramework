package com.kuma.boot.websocket.spring.websocketsimple;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanghongbin
 */
@ConditionalOnProperty(name = "zebra.web.websocket.enabled", havingValue = "true")
@Configuration
@EnableWebSocket // 开启 websocket
public class WebSocketConfig {

   @Bean
   public WebSocketConfigurer webSocketConfigurer(
           @Autowired WebSocketEmitterService webSocketEmitterService,
           @Autowired(required = false) TokenHandler tokenHandler,
           @Autowired(required = false) List<? extends WebSocketMessageListener<?>> messageListeners) {
      if (tokenHandler == null) {
         tokenHandler = new DefaultTokenHandler();
      }
      if (CollUtil.isEmpty(messageListeners)) {
         messageListeners = new ArrayList<>();
      }
      TokenHandler finalTokenHandler = tokenHandler;
      WebSocketSessionHandlerDecorator webSocketSessionHandlerDecorator = new WebSocketSessionHandlerDecorator(
              new JsonWebSocketMessageHandler(messageListeners), webSocketEmitterService);
      return registry -> registry
              // 添加 WebSocketHandler
              .addHandler(webSocketSessionHandlerDecorator, "/websocket")
              .addInterceptors(new LoginInfoHandshakeInterceptor(finalTokenHandler))
              // 允许跨域，否则前端连接会直接断开
              .setAllowedOriginPatterns("*");
   }

   @Bean
   public WebSocketEmitterService webSocketEmitterService() {
      return new WebSocketEmitterService();
   }
}
