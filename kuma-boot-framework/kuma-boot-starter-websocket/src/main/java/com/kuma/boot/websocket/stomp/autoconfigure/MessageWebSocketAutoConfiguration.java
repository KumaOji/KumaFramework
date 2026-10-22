package com.kuma.boot.websocket.stomp.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.websocket.stomp.autoconfigure.properties.WebSocketStompProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableConfigurationProperties({WebSocketStompProperties.class})
@Import({WebSocketProcessorAutoConfiguration.class, WebSocketMessageBrokerAutoConfiguration.class})
@AutoConfiguration(afterName = "org.redisson.spring.starter.RedissonAutoConfigurationV4")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(WebSocketMessageBrokerConfigurer.class)
@ConditionalOnProperty(
   prefix = "kuma.boot.websocket.stomp",
   name = {"enabled"},
   havingValue = "true"
)
public class MessageWebSocketAutoConfiguration implements InitializingBean {
   private static final Logger log = LoggerFactory.getLogger(MessageWebSocketAutoConfiguration.class);

   public MessageWebSocketAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(MessageWebSocketAutoConfiguration.class, "kuma-boot-starter-websocket", new String[0]);
   }

   @PostConstruct
   public void postConstruct() {
      log.debug("[Websocket] |- SDK [Message WebSocket] Auto Configure.");
   }
}
