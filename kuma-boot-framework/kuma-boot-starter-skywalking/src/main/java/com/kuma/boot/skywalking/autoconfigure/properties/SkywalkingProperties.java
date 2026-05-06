package com.kuma.boot.skywalking.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kuma.boot.skywalking")
public class SkywalkingProperties {
   public static final String PREFIX = "kuma.boot.skywalking";
   private Boolean enabled = false;

   public SkywalkingProperties() {
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }
}
