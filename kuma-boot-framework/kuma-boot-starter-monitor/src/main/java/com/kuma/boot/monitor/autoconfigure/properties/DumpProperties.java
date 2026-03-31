package com.kuma.boot.monitor.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.monitor.dump"
)
public class DumpProperties {
   public static final String PREFIX = "kuma.boot.monitor.dump";
   private boolean enabled = true;

   public DumpProperties() {
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
