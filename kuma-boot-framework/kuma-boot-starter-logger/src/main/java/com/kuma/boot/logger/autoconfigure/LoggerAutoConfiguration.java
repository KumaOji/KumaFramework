package com.kuma.boot.logger.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.logger.autoconfigure.properties.LoggerProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties({LoggerProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.logger",
   name = {"enabled"},
   havingValue = "true"
)
public class LoggerAutoConfiguration implements InitializingBean {
   public LoggerAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(LoggerAutoConfiguration.class, "kuma-boot-starter-logger", new String[0]);
   }
}
