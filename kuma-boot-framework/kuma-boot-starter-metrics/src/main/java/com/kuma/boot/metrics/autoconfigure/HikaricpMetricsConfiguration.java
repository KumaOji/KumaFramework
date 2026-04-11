package com.taotao.boot.metrics.autoconfigure;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.hikaricp.v3_0.HikariTelemetry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass({HikariTelemetry.class, OpenTelemetry.class})
public class HikaricpMetricsConfiguration {
   public HikaricpMetricsConfiguration() {
   }

   @Bean
   @ConditionalOnBean({HikariDataSource.class, OpenTelemetry.class})
   @ConditionalOnMissingBean({MetricsTrackerFactory.class})
   public MetricsTrackerFactory openTelemetryMetricsTrackerFactory(OpenTelemetry openTelemetry) {
      return HikariTelemetry.create(openTelemetry).createMetricsTrackerFactory();
   }
}
