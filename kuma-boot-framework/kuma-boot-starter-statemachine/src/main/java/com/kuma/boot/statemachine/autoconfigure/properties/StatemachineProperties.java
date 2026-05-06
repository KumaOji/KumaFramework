package com.kuma.boot.statemachine.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kuma.boot.statemachine")
public class StatemachineProperties {
   public static final String PREFIX = "kuma.boot.statemachine";
   private Boolean enabled = false;

   public StatemachineProperties() {
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }
}
