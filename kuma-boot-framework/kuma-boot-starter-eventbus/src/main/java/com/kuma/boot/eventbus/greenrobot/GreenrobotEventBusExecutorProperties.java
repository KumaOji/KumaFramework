package com.kuma.boot.eventbus.greenrobot;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.eventbus.greenrobot.executor"
)
public class GreenrobotEventBusExecutorProperties {
   private int corePoolSize = 5;
   private int maxPoolSize = 20;
   private int keepAliveSeconds = 60;
   private int queueCapacity = 1000;
   private boolean allowCoreThreadTimeOut = false;
   private String threadNamePrefix = "event-bus-pool-";

   public GreenrobotEventBusExecutorProperties() {
   }

   public int getCorePoolSize() {
      return this.corePoolSize;
   }

   public void setCorePoolSize(int corePoolSize) {
      this.corePoolSize = corePoolSize;
   }

   public int getMaxPoolSize() {
      return this.maxPoolSize;
   }

   public void setMaxPoolSize(int maxPoolSize) {
      this.maxPoolSize = maxPoolSize;
   }

   public int getKeepAliveSeconds() {
      return this.keepAliveSeconds;
   }

   public void setKeepAliveSeconds(int keepAliveSeconds) {
      this.keepAliveSeconds = keepAliveSeconds;
   }

   public int getQueueCapacity() {
      return this.queueCapacity;
   }

   public void setQueueCapacity(int queueCapacity) {
      this.queueCapacity = queueCapacity;
   }

   public boolean isAllowCoreThreadTimeOut() {
      return this.allowCoreThreadTimeOut;
   }

   public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
      this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
   }

   public String getThreadNamePrefix() {
      return this.threadNamePrefix;
   }

   public void setThreadNamePrefix(String threadNamePrefix) {
      this.threadNamePrefix = threadNamePrefix;
   }
}
