package com.kuma.boot.logger.eden.logging.env;

import com.kuma.boot.logger.eden.access.config.AccessLogConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Role;

@ConfigurationProperties(
   prefix = "logging.access"
)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class AccessLogProperties extends AccessLogConfig {
   public static final String PREFIX = "logging.access";
   private boolean enabled = false;

   public AccessLogProperties() {
   }
}
