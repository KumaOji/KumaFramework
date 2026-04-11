package com.kuma.boot.websocket.spring.autoconfigure.config;

import com.kuma.boot.websocket.spring.common.distribute.MessageDistributor;
import com.kuma.boot.websocket.spring.common.distribute.RocketmqMessageDistributor;
import com.kuma.boot.websocket.spring.common.session.WebSocketSessionStore;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(
   prefix = "kuma.boot.websocket.spring",
   name = {"message-distributor"},
   havingValue = "rocketmq"
)
@Configuration(
   proxyBeanMethods = false
)
public class RocketMqMessageDistributorConfig {
   private final WebSocketSessionStore webSocketSessionStore;

   public RocketMqMessageDistributorConfig(WebSocketSessionStore webSocketSessionStore) {
      this.webSocketSessionStore = webSocketSessionStore;
   }

   @Bean
   @ConditionalOnMissingBean({MessageDistributor.class})
   public RocketmqMessageDistributor messageDistributor(RocketMQTemplate template) {
      return new RocketmqMessageDistributor(this.webSocketSessionStore, template);
   }
}
