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
import org.springframework.util.StringUtils;

@Role(2)
@Configuration(
   proxyBeanMethods = false
)
public class AccessLogConfiguration implements ImportAware {
   private static final String IMPORTING_META_NOT_FOUND = "@EnableAccessLog is not present on importing class";

   /**
    * 未配置 {@code logging.access.expression} 且注解未给出有效表达式时的默认切点（Web 控制器层）。
    * 避免 {@link org.springframework.aop.aspectj.AspectJExpressionPointcut} 在 expression 为 null 时启动失败。
    */
   private static final String DEFAULT_ACCESS_LOG_POINTCUT_EXPRESSION =
         "@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller)";

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
      String expression = this.resolvePointcutExpression(this.getAccessLogConfig(configs));
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
      return accessLogConfigs.getIfUnique(() -> {
         AccessLogConfig config = new AccessLogConfig();
         if (this.annotation != null) {
            config.setExpression(this.annotation.getString("expression"));
         }
         return config;
      });
   }

   private String resolvePointcutExpression( AccessLogConfig config ) {
      String expression = config != null ? config.getExpression() : null;
      if (StringUtils.hasText(expression)) {
         return expression;
      }
      if (this.annotation != null) {
         String fromAnnotation = this.annotation.getString("expression");
         if (StringUtils.hasText(fromAnnotation)) {
            return fromAnnotation;
         }
      }
      return DEFAULT_ACCESS_LOG_POINTCUT_EXPRESSION;
   }
}
