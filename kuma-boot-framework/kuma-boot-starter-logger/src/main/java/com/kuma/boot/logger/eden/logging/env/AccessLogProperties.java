package com.kuma.boot.logger.eden.logging.env;

import com.kuma.boot.logger.eden.access.config.AccessLogConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "logging.access"
)
public class AccessLogProperties extends AccessLogConfig {
   public static final String PREFIX = "logging.access";
   private boolean enabled = false;

   public AccessLogProperties() {
   }
}
