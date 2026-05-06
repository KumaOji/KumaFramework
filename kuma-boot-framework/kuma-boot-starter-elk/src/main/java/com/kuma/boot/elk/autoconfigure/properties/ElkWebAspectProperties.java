package com.kuma.boot.elk.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.elk.web.aspect"
)
public class ElkWebAspectProperties {
   public static final String PREFIX = "kuma.boot.elk.web.aspect";
   private boolean enabled = false;

   public ElkWebAspectProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
