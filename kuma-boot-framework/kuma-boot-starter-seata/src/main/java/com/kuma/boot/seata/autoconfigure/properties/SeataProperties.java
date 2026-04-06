package com.kuma.boot.seata.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.seata"
)
public class SeataProperties {
   public static final String PREFIX = "kuma.boot.seata";
   private boolean enabled = false;

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
