package com.kuma.boot.monitor.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.monitor"
)
public class MonitorProperties {
   public static final String PREFIX = "kuma.boot.monitor";
   private boolean enabled = true;
   private int timeSpan = 100;

   public MonitorProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public int getTimeSpan() {
      return this.timeSpan;
   }

   public void setTimeSpan(int timeSpan) {
      this.timeSpan = timeSpan;
   }
}
