package com.kuma.boot.monitor.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.monitor.ping"
)
public class PingProperties {
   public static final String PREFIX = "kuma.boot.monitor.ping";
   private boolean enabled = true;

   public PingProperties() {
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
