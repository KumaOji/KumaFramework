package com.kuma.boot.logger.autoconfigure;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

@AutoConfiguration
@ConditionalOnBean(OpenTelemetry.class)
public class OpenTelemetryAppenderAutoConfiguration implements InitializingBean {
   private final OpenTelemetry openTelemetry;

   OpenTelemetryAppenderAutoConfiguration(OpenTelemetry openTelemetry) {
      this.openTelemetry = openTelemetry;
   }

   public void afterPropertiesSet() {
      OpenTelemetryAppender.install(this.openTelemetry);
   }
}
