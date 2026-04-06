package com.kuma.boot.retry.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.retry.aop.GuavaRetryingAspect;
import com.kuma.boot.retry.autoconfigure.properties.RetryProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({RetryProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.retry",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
public class RetryAutoConfiguration implements InitializingBean {
   public void afterPropertiesSet() throws Exception {
      LogUtils.started(RetryAutoConfiguration.class, "kuma-boot-starter-retry", new String[0]);
   }

   @Bean
   public GuavaRetryingAspect guavaRetryingAspect() {
      return new GuavaRetryingAspect();
   }
}
