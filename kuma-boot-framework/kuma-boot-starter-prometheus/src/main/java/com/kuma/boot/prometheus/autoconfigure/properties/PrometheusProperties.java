package com.kuma.boot.prometheus.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.prometheus"
)
public class PrometheusProperties {
   public static final String PREFIX = "kuma.boot.prometheus";
   private boolean enabled = true;

   public PrometheusProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
