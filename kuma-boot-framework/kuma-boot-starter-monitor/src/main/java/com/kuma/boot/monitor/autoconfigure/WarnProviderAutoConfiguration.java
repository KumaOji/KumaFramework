package com.kuma.boot.monitor.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.Monitor;
import com.kuma.boot.monitor.autoconfigure.properties.WarnProperties;
import com.kuma.boot.monitor.strategy.DefaultWarnStrategy;
import com.kuma.boot.monitor.strategy.Rule;
import com.kuma.boot.monitor.strategy.WarnStrategy;
import com.kuma.boot.monitor.strategy.WarnTemplate;
import com.kuma.boot.monitor.warn.WarnProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({WarnProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.monitor.warn",
   name = {"enabled"},
   havingValue = "true"
)
public class WarnProviderAutoConfiguration implements InitializingBean {
   public WarnProviderAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(WarnProviderAutoConfiguration.class, "kuma-boot-starter-monitor", new String[0]);
   }

   @Bean
   public WarnStrategy defaultWarnStrategy() {
      WarnTemplate warnTemplate = (new WarnTemplate()).register("", "\u53c2\u6570:{name}({desc}),\u547d\u4e2d\u89c4\u5219:{rule},\u5f53\u524d\u503c\uff1a{value}");
      return new DefaultWarnStrategy(warnTemplate, new Rule.RulesAnalyzer());
   }

   @ConditionalOnBean
   @Bean(
      destroyMethod = "close"
   )
   public WarnProvider getWarnProvider(WarnProperties warnProperties, Monitor monitor) {
      return new WarnProvider(warnProperties, monitor);
   }
}
