package com.kuma.boot.logger.eden.logging.autoconfigure;

import com.kuma.boot.logger.eden.EnableBootstrapLog;
import com.kuma.boot.logger.eden.logging.env.BootstrapLogProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@ConditionalOnProperty(
   prefix = "logging.bootstrap",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
@EnableConfigurationProperties({BootstrapLogProperties.class})
@Role(2)
@Configuration(
   proxyBeanMethods = false
)
@EnableBootstrapLog
public class BootstrapLogAutoConfiguration {
   public BootstrapLogAutoConfiguration() {
   }
}
