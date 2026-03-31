package com.kuma.boot.monitor.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.autoconfigure.properties.PingProperties;
import com.kuma.boot.monitor.ping.PingFilter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({PingProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.monitor.ping",
   name = {"enabled"},
   havingValue = "true"
)
public class PingProviderAutoConfiguration implements InitializingBean {
   public PingProviderAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(PingProviderAutoConfiguration.class, "kuma-boot-starter-monitor", new String[0]);
   }

   @Bean
   @ConditionalOnWebApplication(
      type = Type.SERVLET
   )
   public FilterRegistrationBean<PingFilter> pingFilter() {
      FilterRegistrationBean<PingFilter> filterRegistrationBean = new FilterRegistrationBean();
      filterRegistrationBean.setOrder(Integer.MIN_VALUE);
      filterRegistrationBean.setFilter(new PingFilter());
      filterRegistrationBean.setName(PingFilter.class.getName());
      filterRegistrationBean.addUrlPatterns(new String[]{"/health/ping"});
      return filterRegistrationBean;
   }
}
