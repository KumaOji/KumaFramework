package com.kuma.boot.websocket.original;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisMessageListenerConfig {
   private final RedisReceiver redisReceiver;

   public RedisMessageListenerConfig(RedisReceiver redisReceiver) {
      this.redisReceiver = redisReceiver;
   }

   @Bean
   public RedisMessageListenerContainer getRedisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
      RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
      redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
      redisMessageListenerContainer.addMessageListener(this.messageListenerAdapter(), new PatternTopic("ptp-topic"));
      redisMessageListenerContainer.addMessageListener(this.messageAllListenerAdapter(), new PatternTopic("mh-topic"));
      return redisMessageListenerContainer;
   }

   @Bean
   public MessageListenerAdapter messageListenerAdapter() {
      return new MessageListenerAdapter(this.redisReceiver, "sendMsg");
   }

   @Bean
   public MessageListenerAdapter messageAllListenerAdapter() {
      return new MessageListenerAdapter(this.redisReceiver, "sendAllMsg");
   }
}
