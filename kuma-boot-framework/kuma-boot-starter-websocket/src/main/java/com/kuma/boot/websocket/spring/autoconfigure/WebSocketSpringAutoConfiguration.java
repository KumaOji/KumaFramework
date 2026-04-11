package com.kuma.boot.websocket.spring.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.websocket.spring.autoconfigure.config.LocalMessageDistributorConfig;
import com.kuma.boot.websocket.spring.autoconfigure.config.RedisMessageDistributorConfig;
import com.kuma.boot.websocket.spring.autoconfigure.config.RocketMqMessageDistributorConfig;
import com.kuma.boot.websocket.spring.autoconfigure.config.WebSocketHandlerConfig;
import com.kuma.boot.websocket.spring.autoconfigure.properties.WebSocketSpringProperties;
import com.kuma.boot.websocket.spring.common.handler.JsonMessageHandler;
import com.kuma.boot.websocket.spring.common.handler.PingJsonMessageHandler;
import com.kuma.boot.websocket.spring.common.holder.JsonMessageHandlerInitializer;
import com.kuma.boot.websocket.spring.common.message.JsonWebSocketMessage;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.server.HandshakeInterceptor;

@AutoConfiguration
@Import({WebSocketHandlerConfig.class, LocalMessageDistributorConfig.class, RedisMessageDistributorConfig.class, RocketMqMessageDistributorConfig.class})
@EnableWebSocket
@EnableConfigurationProperties({WebSocketSpringProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.websocket.spring",
   name = {"enabled"},
   havingValue = "true"
)
public class WebSocketSpringAutoConfiguration implements InitializingBean {
   private final WebSocketSpringProperties webSocketSpringProperties;

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(WebSocketSpringAutoConfiguration.class, "kuma-boot-starter-websocket", new String[0]);
   }

   public WebSocketSpringAutoConfiguration(WebSocketSpringProperties webSocketSpringProperties) {
      this.webSocketSpringProperties = webSocketSpringProperties;
   }

   @Bean
   @ConditionalOnMissingBean
   public WebSocketConfigurer webSocketConfigurer(List<HandshakeInterceptor> handshakeInterceptor, WebSocketHandler webSocketHandler, @Autowired(required = false) SockJsServiceConfigurer sockJsServiceConfigurer) {
      return (registry) -> {
         WebSocketHandlerRegistration registration = registry.addHandler(webSocketHandler, new String[]{this.webSocketSpringProperties.getPath()}).addInterceptors((HandshakeInterceptor[])handshakeInterceptor.toArray(new HandshakeInterceptor[0]));
         String[] allowedOrigins = this.webSocketSpringProperties.getAllowedOrigins();
         if (allowedOrigins != null && allowedOrigins.length > 0) {
            registration.setAllowedOrigins(allowedOrigins);
         }

         String[] allowedOriginPatterns = this.webSocketSpringProperties.getAllowedOriginPatterns();
         if (allowedOriginPatterns != null && allowedOriginPatterns.length > 0) {
            registration.setAllowedOriginPatterns(allowedOriginPatterns);
         }

         if (this.webSocketSpringProperties.isWithSockjs()) {
            SockJsServiceRegistration sockJsServiceRegistration = registration.withSockJS();
            if (sockJsServiceConfigurer != null) {
               sockJsServiceConfigurer.config(sockJsServiceRegistration);
            }
         }

      };
   }

   @Bean
   @ConditionalOnProperty(
      prefix = "kuma.boot.websocket.spring",
      name = {"heartbeat"},
      havingValue = "true",
      matchIfMissing = true
   )
   public PingJsonMessageHandler pingJsonMessageHandler() {
      return new PingJsonMessageHandler();
   }

   @Bean
   @ConditionalOnMissingBean
   public JsonMessageHandlerInitializer jsonMessageHandlerInitializer(List<JsonMessageHandler<? extends JsonWebSocketMessage>> jsonMessageHandlerList) {
      return new JsonMessageHandlerInitializer(jsonMessageHandlerList);
   }
}
