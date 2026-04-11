package com.kuma.boot.websocket.stomp.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.websocket.stomp.autoconfigure.properties.WebSocketStompProperties;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.DelegatingWebSocketMessageBrokerConfiguration;

@EnableConfigurationProperties({WebSocketStompProperties.class})
@Import({WebSocketProcessorAutoConfiguration.class, WebSocketMessageBrokerAutoConfiguration.class, WebSocketLogicAutoConfiguration.class})
@AutoConfiguration(
   after = {DelegatingWebSocketMessageBrokerConfiguration.class}
)
@ConditionalOnBean({RedissonClient.class})
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
