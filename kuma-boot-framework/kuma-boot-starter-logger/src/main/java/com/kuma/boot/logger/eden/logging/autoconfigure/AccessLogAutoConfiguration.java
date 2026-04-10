package com.kuma.boot.logger.eden.logging.autoconfigure;

import com.kuma.boot.logger.eden.EnableAccessLog;
import com.kuma.boot.logger.eden.logging.env.AccessLogProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@ConditionalOnProperty(
   prefix = "logging.access",
   name = {"enabled"},
   havingValue = "true"
)
@EnableConfigurationProperties({AccessLogProperties.class})
@Role(2)
@Configuration(
   proxyBeanMethods = false
)
@EnableAccessLog
public class AccessLogAutoConfiguration {
   public AccessLogAutoConfiguration() {
   }
}
