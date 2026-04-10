package com.kuma.boot.data.p6spy.autoconfigure;

import com.p6spy.engine.spy.P6SpyDriver;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.p6spy.autoconfigure.properties.P6spyProperties;
import com.kuma.boot.data.p6spy.ext.P6spyConfigLoaderBeanPostProcessor;
import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = DataSourceAutoConfiguration.class)
@ConditionalOnClass({P6SpyDriver.class})
@EnableConfigurationProperties({P6spyProperties.class})
@ConditionalOnBean({DataSource.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.data.p6spy",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
public class P6spyAutoConfiguration implements InitializingBean {
   public P6spyAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(P6spyAutoConfiguration.class, "kuma-boot-starter-data-p6spy", new String[0]);
   }

   @Bean
   public static P6spyConfigLoaderBeanPostProcessor cusP6spyConfigLoaderBeanPostProcessor() {
      return new P6spyConfigLoaderBeanPostProcessor();
   }
}
