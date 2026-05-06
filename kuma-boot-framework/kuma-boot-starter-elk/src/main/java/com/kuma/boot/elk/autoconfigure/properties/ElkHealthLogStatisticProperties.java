package com.kuma.boot.elk.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.elk.log.statistic"
)
public class ElkHealthLogStatisticProperties {
   public static final String PREFIX = "kuma.boot.elk.log.statistic";
   private boolean enabled = false;

   public ElkHealthLogStatisticProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
