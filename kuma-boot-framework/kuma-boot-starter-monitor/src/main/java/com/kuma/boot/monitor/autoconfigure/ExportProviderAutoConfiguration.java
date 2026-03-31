package com.kuma.boot.monitor.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.Monitor;
import com.kuma.boot.monitor.autoconfigure.properties.ExportProperties;
import com.kuma.boot.monitor.collect.HealthCheckProvider;
import com.kuma.boot.monitor.export.ExportProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({ExportProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.monitor.export",
   name = {"enabled"},
   havingValue = "true"
)
public class ExportProviderAutoConfiguration implements InitializingBean {
   public ExportProviderAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(ExportProviderAutoConfiguration.class, "kuma-boot-starter-monitor", new String[0]);
   }

   @Bean(
      initMethod = "start",
      destroyMethod = "close"
   )
   @ConditionalOnBean
   public ExportProvider getExportProvider(Monitor monitor, ExportProperties exportProperties, HealthCheckProvider healthCheckProvider) {
      return new ExportProvider(monitor, exportProperties, healthCheckProvider);
   }
}
