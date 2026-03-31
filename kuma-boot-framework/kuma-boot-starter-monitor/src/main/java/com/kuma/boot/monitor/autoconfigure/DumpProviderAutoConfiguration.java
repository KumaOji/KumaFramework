package com.kuma.boot.monitor.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.autoconfigure.properties.DumpProperties;
import com.kuma.boot.monitor.dump.DumpFilter;
import com.kuma.boot.monitor.dump.DumpProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({DumpProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.monitor.dump",
   name = {"enabled"},
   havingValue = "true"
)
public class DumpProviderAutoConfiguration implements InitializingBean {
   public DumpProviderAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(DumpProviderAutoConfiguration.class, "kuma-boot-starter-monitor", new String[0]);
   }

   @Bean
   public DumpProvider dumpProvider() {
      return new DumpProvider();
   }

   @Bean
   @ConditionalOnWebApplication(
      type = Type.SERVLET
   )
   public FilterRegistrationBean<DumpFilter> dumpFilter() {
      FilterRegistrationBean<DumpFilter> filterRegistrationBean = new FilterRegistrationBean();
      filterRegistrationBean.setOrder(-2147483647);
      filterRegistrationBean.setFilter(new DumpFilter());
      filterRegistrationBean.setName(DumpFilter.class.getName());
      filterRegistrationBean.addUrlPatterns(new String[]{"/health/dump/*"});
      return filterRegistrationBean;
   }
}
