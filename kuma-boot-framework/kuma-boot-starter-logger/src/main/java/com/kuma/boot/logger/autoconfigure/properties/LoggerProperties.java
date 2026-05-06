package com.kuma.boot.logger.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.logger"
)
public class LoggerProperties {
   public static final String PREFIX = "kuma.boot.logger";
   private Boolean enabled = false;

   public LoggerProperties() {
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }
}
