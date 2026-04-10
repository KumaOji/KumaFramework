package com.kuma.boot.logger.autoconfigure;

import com.kuma.boot.common.support.property.YamlPropertySourceFactory;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration
@PropertySource(
   factory = YamlPropertySourceFactory.class,
   value = {"classpath:logback-access.yml"}
)
public class LogbackAccessAutoConfiguration implements InitializingBean {
   public LogbackAccessAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(LogbackAccessAutoConfiguration.class, "kuma-boot-starter-logger", new String[0]);
   }
}
