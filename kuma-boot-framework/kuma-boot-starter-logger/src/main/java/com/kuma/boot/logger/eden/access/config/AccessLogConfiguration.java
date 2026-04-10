package com.kuma.boot.logger.eden.access.config;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.logger.eden.EnableAccessLog;
import com.kuma.boot.logger.eden.access.aop.AccessLogAdvisor;
import com.kuma.boot.logger.eden.access.aop.AccessLogInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

@Role(2)
@Configuration(
   proxyBeanMethods = false
)
public class AccessLogConfiguration implements ImportAware {
   private static final String IMPORTING_META_NOT_FOUND = "@EnableAccessLog is not present on importing class";
   private AnnotationAttributes annotation;

   public AccessLogConfiguration() {
   }

   public void setImportMetadata(AnnotationMetadata importMetadata) {
      this.annotation = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableAccessLog.class.getName(), false));
      if (this.annotation == null) {
         LogUtils.warn("@EnableAccessLog is not present on importing class", new Object[0]);
      }

   }

   @Bean
   public AccessLogAdvisor accessLogAdvisor(ObjectProvider<AccessLogConfig> configs, AccessLogInterceptor interceptor) {
      AccessLogAdvisor advisor = new AccessLogAdvisor();
      String expression = this.getAccessLogConfig(configs).getExpression();
      advisor.setExpression(expression);
      advisor.setAdvice(interceptor);
      if (this.annotation != null) {
         advisor.setOrder((Integer)this.annotation.getNumber("order"));
      }

      return advisor;
   }

   @Bean
   public AccessLogInterceptor accessLogInterceptor(ObjectProvider<AccessLogConfig> configs) {
      return new AccessLogInterceptor(this.getAccessLogConfig(configs));
   }

   private AccessLogConfig getAccessLogConfig(ObjectProvider<AccessLogConfig> accessLogConfigs) {
      return (AccessLogConfig)accessLogConfigs.getIfUnique(() -> {
         AccessLogConfig config = new AccessLogConfig();
         config.setExpression(this.annotation.getString("expression"));
         return config;
      });
   }
}
