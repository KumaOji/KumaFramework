package com.kuma.boot.tracer.autoconfigure;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

@AutoConfiguration
public class SleuthDataSourceAutoConfiguration implements InitializingBean, EnvironmentAware {
   private Environment environment;

   public SleuthDataSourceAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      String jdbcUrl = this.environment.getProperty("spring.datasource.url");
      jdbcUrl = jdbcUrl + "&queryInterceptors=brave.mysql8.TracingQueryInterceptor&exceptionInterceptors=brave.mysql8.TracingExceptionInterceptor";
   }

   public void setEnvironment(Environment environment) {
      this.environment = environment;
   }
}
