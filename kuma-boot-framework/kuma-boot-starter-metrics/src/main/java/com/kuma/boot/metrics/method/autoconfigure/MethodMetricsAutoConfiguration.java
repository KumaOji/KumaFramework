package com.kuma.boot.metrics.method.autoconfigure;

import com.kuma.boot.metrics.method.aspect.MethodMetricsAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass({MeterRegistry.class})
@EnableConfigurationProperties({MethodMetricsProperties.class})
@ConditionalOnProperty(
   prefix = "method.metrics",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
public class MethodMetricsAutoConfiguration {
   public MethodMetricsAutoConfiguration() {
   }

   @Bean
   @ConditionalOnMissingBean
   public MethodMetricsAspect methodMetricsAspect(MeterRegistry meterRegistry, MethodMetricsProperties properties) {
      return new MethodMetricsAspect(meterRegistry, properties);
   }
}
