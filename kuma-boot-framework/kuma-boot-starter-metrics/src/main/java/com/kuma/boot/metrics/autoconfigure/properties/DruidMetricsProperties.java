package com.kuma.boot.metrics.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.metrics.druid"
)
public class DruidMetricsProperties {
   public static final String PREFIX = "kuma.boot.metrics.druid";
   private boolean enabled = false;

   public DruidMetricsProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
