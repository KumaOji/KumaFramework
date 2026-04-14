package com.kuma.boot.websocket.stomp.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.websocket.stomp.autoconfigure.properties.WebSocketStompProperties;
import com.kuma.boot.websocket.stomp.core.BearerTokenResolver;
import com.kuma.boot.websocket.stomp.interceptor.WebSocketChannelInterceptor;
import com.kuma.boot.websocket.stomp.interceptor.WebSocketSessionHandshakeInterceptor;
import com.kuma.boot.websocket.stomp.processor.WebSocketBearerTokenResolver;
import com.kuma.boot.websocket.stomp.processor.WebSocketClusterProcessor;
import com.kuma.boot.websocket.stomp.processor.WebSocketMessageSender;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;

@Configuration(
   proxyBeanMethods = false
)
public class WebSocketProcessorAutoConfiguration {
   private static final Logger log = LoggerFactory.getLogger(WebSocketProcessorAutoConfiguration.class);
   private final WebSocketStompProperties webSocketStompProperties;
   private final RedissonClient redissonClient;

   // 构造器不注入 SimpUserRegistry，避免与 DelegatingWebSocketMessageBrokerConfiguration 形成循环依赖；在 webSocketMessageSender 方法参数中注入即可。
   public WebSocketProcessorAutoConfiguration(WebSocketStompProperties webSocketStompProperties, RedissonClient redissonClient) {
      this.webSocketStompProperties = webSocketStompProperties;
      this.redissonClient = redissonClient;
   }

   @PostConstruct
   public void postConstruct() {
      log.debug("[Websocket] |- SDK [WebSocket Processor] Auto Configure.");
   }

   @Bean
   @ConditionalOnMissingBean
   public WebSocketChannelInterceptor webSocketChannelInterceptor() {
      WebSocketChannelInterceptor webSocketChannelInterceptor = new WebSocketChannelInterceptor();
      log.trace("[Websocket] |- Bean [Web Socket Channel Interceptor] Auto Configure.");
      return webSocketChannelInterceptor;
   }

   @Bean
   @ConditionalOnMissingBean
   public BearerTokenResolver principalResolver() {
      WebSocketBearerTokenResolver webSocketPrincipalResolver = new WebSocketBearerTokenResolver();
      log.trace("[Websocket] |- Bean [WebSocket Principal Resolver] Auto Configure.");
      return webSocketPrincipalResolver;
   }

   @Bean
   @ConditionalOnBean({BearerTokenResolver.class})
   public WebSocketSessionHandshakeInterceptor webSocketSessionHandshakeInterceptor(BearerTokenResolver bearerTokenResolver) {
      WebSocketSessionHandshakeInterceptor webSocketSessionHandshakeInterceptor = new WebSocketSessionHandshakeInterceptor(bearerTokenResolver);
      log.trace("[Websocket] |- Bean [Web Socket Session Handshake Interceptor] Auto Configure.");
      return webSocketSessionHandshakeInterceptor;
   }

   @Bean
   public WebSocketMessageSender webSocketMessageSender(SimpMessagingTemplate simpMessagingTemplate, SimpUserRegistry simpUserRegistry) {
      WebSocketMessageSender webSocketMessageSender = new WebSocketMessageSender();
      webSocketMessageSender.setSimpMessagingTemplate(simpMessagingTemplate);
      webSocketMessageSender.setSimpUserRegistry(simpUserRegistry);
      log.trace("[Websocket] |- Bean [Web Socket Message Sender] Auto Configure.");
      return webSocketMessageSender;
   }

   @Bean
   public WebSocketClusterProcessor webSocketClusterProcessor(WebSocketMessageSender webSocketMessageSender) {
      WebSocketClusterProcessor webSocketClusterProcessor = new WebSocketClusterProcessor();
      webSocketClusterProcessor.setWebSocketProperties(this.webSocketStompProperties);
      webSocketClusterProcessor.setWebSocketMessageSender(webSocketMessageSender);
      webSocketClusterProcessor.setRedissonClient(this.redissonClient);
      LogUtils.info("Bean [Web Socket Cluster Processor] Auto Configure.", new Object[0]);
      return webSocketClusterProcessor;
   }
}
