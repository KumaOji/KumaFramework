package com.kuma.boot.tracer.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.micrometer"
)
public class MicrometerProperties {
   public static final String PREFIX = "kuma.boot.micrometer";
   private Boolean enabled = false;

   public MicrometerProperties() {
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }
}
