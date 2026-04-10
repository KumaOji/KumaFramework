package com.kuma.boot.logger.eden.logging.env;

import com.kuma.boot.logger.eden.bootstrap.config.BootstrapLogConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "logging.bootstrap"
)
public class BootstrapLogProperties extends BootstrapLogConfig {
   public static final String PREFIX = "logging.bootstrap";
   public static final String ENABLED = "logging.bootstrap.enabled";
   private boolean enabled = true;

   public BootstrapLogProperties() {
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
