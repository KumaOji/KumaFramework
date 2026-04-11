package com.kuma.boot.eventbus.atlas.config;

import com.kuma.boot.eventbus.atlas.persistence.DatabaseEventPersistence;
import com.kuma.boot.eventbus.atlas.persistence.EventPersistence;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventPersistenceConfiguration {
   public EventPersistenceConfiguration() {
   }

   @Bean
   @ConditionalOnMissingBean
   public EventPersistence eventPersistence() {
      return new DatabaseEventPersistence();
   }
}
