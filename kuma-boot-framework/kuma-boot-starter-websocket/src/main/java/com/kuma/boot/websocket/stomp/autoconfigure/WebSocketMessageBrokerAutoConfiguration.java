package com.kuma.boot.websocket.stomp.autoconfigure;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.websocket.stomp.autoconfigure.properties.WebSocketStompProperties;
import com.kuma.boot.websocket.stomp.interceptor.WebSocketChannelInterceptor;
import com.kuma.boot.websocket.stomp.interceptor.WebSocketPrincipalHandshakeHandler;
import com.kuma.boot.websocket.stomp.interceptor.WebSocketSessionHandshakeInterceptor;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.session.Session;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.HandshakeInterceptor;

@AutoConfiguration(
   after = {WebSocketSessionHandshakeInterceptor.class}
)
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerAutoConfiguration extends AbstractSessionWebSocketMessageBrokerConfigurer<Session> {
   private static final Logger log = LoggerFactory.getLogger(WebSocketMessageBrokerAutoConfiguration.class);
   private final WebSocketStompProperties webSocketStompProperties;
   private final WebSocketChannelInterceptor webSocketChannelInterceptor;
   private final WebSocketSessionHandshakeInterceptor webSocketSessionHandshakeInterceptor;

   public WebSocketMessageBrokerAutoConfiguration(WebSocketStompProperties webSocketStompProperties, WebSocketChannelInterceptor webSocketChannelInterceptor, WebSocketSessionHandshakeInterceptor webSocketSessionHandshakeInterceptor) {
      this.webSocketStompProperties = webSocketStompProperties;
      this.webSocketChannelInterceptor = webSocketChannelInterceptor;
      this.webSocketSessionHandshakeInterceptor = webSocketSessionHandshakeInterceptor;
   }

   @PostConstruct
   public void postConstruct() {
      log.debug("[Websocket] |- SDK [WebSocket Message Broker] Auto Configure.");
   }

   protected void configureStompEndpoints(StompEndpointRegistry registry) {
      WebSocketPrincipalHandshakeHandler principalHandshakeHandler = new WebSocketPrincipalHandshakeHandler();
      registry.addEndpoint(new String[]{this.webSocketStompProperties.getEndpoint()}).setAllowedOrigins(new String[]{"*"}).addInterceptors(new HandshakeInterceptor[]{this.webSocketSessionHandshakeInterceptor}).setHandshakeHandler(principalHandshakeHandler).withSockJS();
      registry.addEndpoint(new String[]{this.webSocketStompProperties.getEndpoint()}).setAllowedOrigins(new String[]{"*"}).addInterceptors(new HandshakeInterceptor[]{this.webSocketSessionHandshakeInterceptor}).setHandshakeHandler(principalHandshakeHandler);
   }

   public void configureMessageBroker(MessageBrokerRegistry registry) {
      ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
      taskScheduler.setPoolSize(1);
      taskScheduler.setThreadNamePrefix("Websocket-websocket-heartbeat-thread-");
      taskScheduler.initialize();
      registry.enableSimpleBroker(new String[]{"/broadcast", "/personal"}).setHeartbeatValue(new long[]{10000L, 10000L}).setTaskScheduler(taskScheduler);
      String[] applicationDestinationPrefixes = this.webSocketStompProperties.getApplicationPrefixes();
      if (ArrayUtils.isNotEmpty(applicationDestinationPrefixes)) {
         registry.setApplicationDestinationPrefixes(applicationDestinationPrefixes);
      }

      if (StringUtils.isNotBlank(this.webSocketStompProperties.getUserDestinationPrefix())) {
         registry.setUserDestinationPrefix(this.webSocketStompProperties.getUserDestinationPrefix());
      }

   }

   public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
      registration.setMessageSizeLimit(10240).setSendBufferSizeLimit(10240).setSendTimeLimit(10000);
      super.configureWebSocketTransport(registration);
   }

   public void configureClientInboundChannel(ChannelRegistration registration) {
      registration.taskExecutor().corePoolSize(10).maxPoolSize(20).keepAliveSeconds(60);
      registration.interceptors(new ChannelInterceptor[]{this.webSocketChannelInterceptor});
      super.configureClientInboundChannel(registration);
   }

   public void configureClientOutboundChannel(ChannelRegistration registration) {
      registration.taskExecutor().corePoolSize(10).maxPoolSize(20).keepAliveSeconds(60);
   }

   public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
      super.addArgumentResolvers(argumentResolvers);
   }

   public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
      super.addReturnValueHandlers(returnValueHandlers);
   }

   public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
      return super.configureMessageConverters(messageConverters);
   }
}
