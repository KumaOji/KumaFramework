package com.kuma.boot.monitor.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.monitor.threadpool"
)
public class MonitorThreadPoolProperties {
   public static final String PREFIX = "kuma.boot.monitor.threadpool";
   private int corePoolSize = 10;
   private int maximumPoolSize = 50;
   private long keepAliveTime = 60L;
   private String threadNamePrefix = "kmc-monitor-executor";
   private boolean checkHealth = true;

   public MonitorThreadPoolProperties() {
   }

   public int getCorePoolSize() {
      return this.corePoolSize;
   }

   public void setCorePoolSize(int corePoolSize) {
      this.corePoolSize = corePoolSize;
   }

   public int getMaximumPoolSize() {
      return this.maximumPoolSize;
   }

   public void setMaximumPoolSize(int maximumPoolSize) {
      this.maximumPoolSize = maximumPoolSize;
   }

   public long getKeepAliveTime() {
      return this.keepAliveTime;
   }

   public void setKeepAliveTime(long keepAliveTime) {
      this.keepAliveTime = keepAliveTime;
   }

   public String getThreadNamePrefix() {
      return this.threadNamePrefix;
   }

   public void setThreadNamePrefix(String threadNamePrefix) {
      this.threadNamePrefix = threadNamePrefix;
   }

   public boolean isCheckHealth() {
      return this.checkHealth;
   }

   public void setCheckHealth(boolean checkHealth) {
      this.checkHealth = checkHealth;
   }
}
