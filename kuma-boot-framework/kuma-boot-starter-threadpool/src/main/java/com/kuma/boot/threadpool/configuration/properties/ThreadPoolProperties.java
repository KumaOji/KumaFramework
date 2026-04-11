package com.kuma.boot.threadpool.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.threadpool"
)
public class ThreadPoolProperties {
   public static final String PREFIX = "kuma.boot.threadpool";
   private boolean enabled = false;

   public ThreadPoolProperties() {
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
