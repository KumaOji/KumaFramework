package com.kuma.boot.eventbus.atlas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "atlas.event"
)
public class EventProperties {
   private int threadPoolSize = 10;
   private String persistenceType = "database";
   private boolean enableAsync = true;
   private int maxRetryAttempts = 3;

   public EventProperties() {
   }

   public int getThreadPoolSize() {
      return this.threadPoolSize;
   }

   public void setThreadPoolSize(int threadPoolSize) {
      this.threadPoolSize = threadPoolSize;
   }

   public String getPersistenceType() {
      return this.persistenceType;
   }

   public void setPersistenceType(String persistenceType) {
      this.persistenceType = persistenceType;
   }

   public boolean isEnableAsync() {
      return this.enableAsync;
   }

   public void setEnableAsync(boolean enableAsync) {
      this.enableAsync = enableAsync;
   }

   public int getMaxRetryAttempts() {
      return this.maxRetryAttempts;
   }

   public void setMaxRetryAttempts(int maxRetryAttempts) {
      this.maxRetryAttempts = maxRetryAttempts;
   }
}
