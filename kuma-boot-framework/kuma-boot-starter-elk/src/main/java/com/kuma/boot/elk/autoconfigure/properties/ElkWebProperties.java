package com.kuma.boot.elk.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.elk.web"
)
public class ElkWebProperties {
   public static final String PREFIX = "kuma.boot.elk.web";
   private boolean enabled = false;

   public ElkWebProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
