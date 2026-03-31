package com.kuma.boot.monitor.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.autoconfigure.CoreAutoConfiguration;
import com.kuma.boot.monitor.Monitor;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.autoconfigure.properties.MonitorProperties;
import com.kuma.boot.monitor.collect.CollectorMetrics;
import com.kuma.boot.monitor.collect.HealthCheckProvider;
import com.kuma.boot.monitor.collect.HealthReportFilter;
import com.kuma.boot.monitor.endpoint.MonitorEndPoint;
import com.kuma.boot.monitor.strategy.WarnStrategy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(
   after = {CoreAutoConfiguration.class, WarnProviderAutoConfiguration.class, MonitorAutoConfiguration.class}
)
@EnableConfigurationProperties({MonitorProperties.class, CollectTaskProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.monitor",
   name = {"enabled"},
   havingValue = "true"
)
public class HealthAutoConfiguration implements InitializingBean {
   public HealthAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(HealthAutoConfiguration.class, "kuma-boot-starter-monitor", new String[0]);
   }

   @Bean(
      destroyMethod = "close"
   )
   public HealthCheckProvider healthCheckProvider(WarnStrategy strategy, CollectTaskProperties collectTaskProperties, MonitorProperties monitorProperties, Monitor monitor) {
      return new HealthCheckProvider(collectTaskProperties, monitorProperties, strategy, monitor);
   }

   @Bean
   public MonitorEndPoint systemHealthEndPoint(HealthCheckProvider healthCheckProvider) {
      return new MonitorEndPoint(healthCheckProvider);
   }

   @Bean
   @ConditionalOnWebApplication(
      type = Type.SERVLET
   )
   public FilterRegistrationBean<HealthReportFilter> healthReportFilter() {
      FilterRegistrationBean<HealthReportFilter> filterRegistrationBean = new FilterRegistrationBean();
      filterRegistrationBean.setOrder(-2147483646);
      filterRegistrationBean.setFilter(new HealthReportFilter());
      filterRegistrationBean.setName(HealthReportFilter.class.getName());
      filterRegistrationBean.addUrlPatterns(new String[]{"/health/report/*"});
      return filterRegistrationBean;
   }

   @Bean
   public CollectorMetrics collectorMetrics(@Autowired(required = false) HealthCheckProvider healthCheckProvider) {
      return new CollectorMetrics(healthCheckProvider);
   }
}
