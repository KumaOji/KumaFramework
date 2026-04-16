package com.kuma.boot.websocket.spring.autoconfigure.config;

import com.kuma.boot.websocket.spring.common.distribute.LocalMessageDistributor;
import com.kuma.boot.websocket.spring.common.distribute.MessageDistributor;
import com.kuma.boot.websocket.spring.common.session.WebSocketSessionStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnBean(WebSocketSessionStore.class)
@ConditionalOnProperty(
   prefix = "kuma.boot.websocket.spring",
   name = {"message-distributor"},
   havingValue = "local",
   matchIfMissing = true
)
@Configuration(
   proxyBeanMethods = false
)
public class LocalMessageDistributorConfig {
   private final WebSocketSessionStore webSocketSessionStore;

   public LocalMessageDistributorConfig(WebSocketSessionStore webSocketSessionStore) {
      this.webSocketSessionStore = webSocketSessionStore;
   }

   @Bean
   @ConditionalOnMissingBean({MessageDistributor.class})
   public LocalMessageDistributor messageDistributor() {
      return new LocalMessageDistributor(this.webSocketSessionStore);
   }
}
