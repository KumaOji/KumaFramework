package com.kuma.boot.metrics.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.otlp"
)
public class OtlpMetricsProperties {
   public static final String PREFIX = "kuma.boot.otlp";
   private boolean enabled = true;

   public OtlpMetricsProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
