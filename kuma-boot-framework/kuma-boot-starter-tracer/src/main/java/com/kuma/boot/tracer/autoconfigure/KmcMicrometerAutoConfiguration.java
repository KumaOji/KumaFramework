package com.kuma.boot.tracer.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.tracer.autoconfigure.properties.MicrometerProperties;
import io.micrometer.common.annotation.ValueExpressionResolver;
import io.micrometer.common.annotation.ValueResolver;
import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.MeterTagAnnotationHandler;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Objects;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;

@EnableConfigurationProperties({MicrometerProperties.class})
@Configuration(
   proxyBeanMethods = false
)
public class KmcMicrometerAutoConfiguration implements InitializingBean {
   public KmcMicrometerAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(KmcMicrometerAutoConfiguration.class, "kuma-boot-starter-tracer", new String[0]);
   }

   @Bean
   public MeterTagAnnotationHandler meterTagAnnotationHandler() {
      return new MeterTagAnnotationHandler((valueResolverClass) -> new ValueResolver() {
            {
               Objects.requireNonNull(KmcMicrometerAutoConfiguration.this);
            }

            public String resolve(Object parameter) {
               LogUtils.info("parameter: {}", new Object[]{parameter});
               return parameter.toString();
            }
         }, (valueExpressionResolverClass) -> new ValueExpressionResolver() {
            {
               Objects.requireNonNull(KmcMicrometerAutoConfiguration.this);
            }

            public String resolve(String expression, Object parameter) {
               LogUtils.info("expression: {}, parameter: {}", new Object[]{expression, parameter});
               return parameter.toString();
            }
         });
   }

   @Bean
   public CountedAspect countedAspect(MeterRegistry meterRegistry) {
      return new CountedAspect(meterRegistry, this::skipNonControllers);
   }

   private boolean skipNonControllers(ProceedingJoinPoint pjp) {
      Class<?> targetClass = pjp.getTarget().getClass();
      return AnnotationUtils.findAnnotation(targetClass, Controller.class) == null;
   }
}
