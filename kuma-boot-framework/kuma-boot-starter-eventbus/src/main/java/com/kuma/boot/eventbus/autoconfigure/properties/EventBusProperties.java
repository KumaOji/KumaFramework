package com.kuma.boot.eventbus.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.eventbus"
)
public class EventBusProperties {
   public static final String PREFIX = "kuma.boot.eventbus";
   private boolean enabled = false;

   public EventBusProperties() {
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
