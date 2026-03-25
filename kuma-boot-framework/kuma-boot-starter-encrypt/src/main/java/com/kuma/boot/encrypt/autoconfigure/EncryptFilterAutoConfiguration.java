package com.kuma.boot.encrypt.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.filter.EncryptFilter;
import com.kuma.boot.encrypt.handler.EncryptHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

@AutoConfiguration
@ConditionalOnProperty(
   prefix = "kuma.boot.encrypt.filter",
   name = {"enabled"},
   havingValue = "true"
)
public class EncryptFilterAutoConfiguration implements InitializingBean {
   private final EncryptHandler encryptHandler;
   private final Environment environment;

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(EncryptFilterAutoConfiguration.class, "kuma-boot-starter-encrypt", new String[0]);
   }

   public EncryptFilterAutoConfiguration(EncryptHandler encryptHandler, Environment environment) {
      this.encryptHandler = encryptHandler;
      this.environment = environment;
   }

   @Bean
   @Conditional({EncryptDebugCondition.class})
   public FilterRegistrationBean encryptFilter() {
      Integer order = (Integer)this.environment.getProperty("encrypt.order", Integer.class);
      FilterRegistrationBean<EncryptFilter> bean = new FilterRegistrationBean();
      bean.setFilter(new EncryptFilter(this.encryptHandler));
      bean.addUrlPatterns(new String[]{"/*"});
      bean.setName("encryptFilter");
      bean.setOrder(order == null ? 0 : order);
      return bean;
   }

   public static class EncryptDebugCondition implements Condition {
      public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
         Environment environment = conditionContext.getEnvironment();
         Boolean debug = (Boolean)environment.getProperty("encrypt.debug", Boolean.class);
         return debug == null || !debug;
      }
   }
}
