package com.kuma.boot.metrics.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.metrics"
)
public class MetricsProperties {
   public static final String PREFIX = "kuma.boot.metrics";
   private boolean enabled = false;

   public MetricsProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
