package com.kuma.boot.data.jpa.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.context.MappingContextEvent;
import org.springframework.data.repository.init.RepositoriesPopulatedEvent;

@AutoConfiguration
public class DataEventListenerAutoConfiguration {
   public DataEventListenerAutoConfiguration() {
   }

   @Configuration
   public static class RepositoriesPopulatedEventListener implements ApplicationListener<RepositoriesPopulatedEvent> {
      public RepositoriesPopulatedEventListener() {
      }

      public void onApplicationEvent(RepositoriesPopulatedEvent event) {
         LogUtils.info("DataEventListener ----- RepositoriesPopulatedEvent onApplicationEvent {}", new Object[]{event});
      }
   }

   @Configuration
   public static class MappingContextEventListener implements ApplicationListener<MappingContextEvent> {
      public MappingContextEventListener() {
      }

      public void onApplicationEvent(MappingContextEvent event) {
         LogUtils.info("DataEventListener ----- MappingContextEvent onApplicationEvent {}", new Object[]{event});
      }
   }
}
