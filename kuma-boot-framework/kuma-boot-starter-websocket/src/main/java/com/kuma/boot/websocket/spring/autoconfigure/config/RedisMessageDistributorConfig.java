package com.kuma.boot.websocket.spring.autoconfigure.config;

import com.kuma.boot.websocket.spring.common.distribute.MessageDistributor;
import com.kuma.boot.websocket.spring.common.distribute.RedisMessageDistributor;
import com.kuma.boot.websocket.spring.common.distribute.RedisMessageListenerInitializer;
import com.kuma.boot.websocket.spring.common.session.WebSocketSessionStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@ConditionalOnClass({StringRedisTemplate.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.websocket.spring",
   name = {"message-distributor"},
   havingValue = "redis"
)
@Configuration(
   proxyBeanMethods = false
)
public class RedisMessageDistributorConfig {
   private final WebSocketSessionStore webSocketSessionStore;

   public RedisMessageDistributorConfig(WebSocketSessionStore webSocketSessionStore) {
      this.webSocketSessionStore = webSocketSessionStore;
   }

   @Bean
   @ConditionalOnMissingBean({MessageDistributor.class})
   public RedisMessageDistributor messageDistributor(StringRedisTemplate stringRedisTemplate) {
      return new RedisMessageDistributor(this.webSocketSessionStore, stringRedisTemplate);
   }

   @Bean
   @ConditionalOnBean({RedisMessageDistributor.class})
   @ConditionalOnMissingBean
   public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
      RedisMessageListenerContainer container = new RedisMessageListenerContainer();
      container.setConnectionFactory(connectionFactory);
      return container;
   }

   @Bean
   @ConditionalOnMissingBean
   public RedisMessageListenerInitializer redisMessageListenerInitializer(RedisMessageListenerContainer redisMessageListenerContainer, RedisMessageDistributor redisWebsocketMessageListener) {
      return new RedisMessageListenerInitializer(redisMessageListenerContainer, redisWebsocketMessageListener);
   }
}
