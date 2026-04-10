package com.kuma.boot.job.xxl.autoconfigure;

import com.kuma.boot.job.xxl.trace.XxlJobBeanPostProcessor;
import io.micrometer.tracing.ScopedSpan;
import io.micrometer.tracing.SpanNamer;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.internal.DefaultSpanNamer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(
   after = {XxlJobAutoConfiguration.class}
)
@ConditionalOnClass({ScopedSpan.class, Tracer.class, SpanNamer.class, DefaultSpanNamer.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.job.xxl",
   name = {"trace"},
   havingValue = "true"
)
public class XxlJobTraceAutoConfiguration {
   public XxlJobTraceAutoConfiguration() {
   }

   @Bean
   public XxlJobBeanPostProcessor xxlJobBeanPostProcessor() {
      return new XxlJobBeanPostProcessor();
   }
}
