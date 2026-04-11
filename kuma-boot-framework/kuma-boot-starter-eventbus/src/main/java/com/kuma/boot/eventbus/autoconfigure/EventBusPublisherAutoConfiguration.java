package com.kuma.boot.eventbus.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.EventListenerRegistry;
import com.kuma.boot.eventbus.publisher.EventBusPublisher;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(
   after = {EventBusAutoConfiguration.class}
)
public class EventBusPublisherAutoConfiguration implements InitializingBean {
   private final Map<String, EventListenerRegistry> eventListenerRegistryMap;

   public EventBusPublisherAutoConfiguration(Map<String, EventListenerRegistry> eventListenerRegistryMap) {
      this.eventListenerRegistryMap = eventListenerRegistryMap;
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(EventBusPublisherAutoConfiguration.class, "kuma-boot-starter-eventbus", new String[0]);
   }

   @Bean
   public EventBusPublisher eventPublisher() {
      return new EventBusPublisher(this.eventListenerRegistryMap);
   }
}
