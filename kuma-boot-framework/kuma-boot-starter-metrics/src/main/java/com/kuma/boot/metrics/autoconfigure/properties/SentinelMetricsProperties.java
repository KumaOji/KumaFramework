package com.kuma.boot.metrics.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.metrics.sentinel"
)
public class SentinelMetricsProperties {
   public static final String PREFIX = "kuma.boot.metrics.sentinel";
   private boolean enabled = false;

   public SentinelMetricsProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
