package com.kuma.boot.prometheus.autoconfigure;

import com.kuma.boot.core.utils.common.PropertyUtils;
import com.kuma.boot.prometheus.autoconfigure.properties.PrometheusProperties;
import com.kuma.boot.prometheus.collector.PrometheusCollector;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import java.time.Duration;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.boot.micrometer.metrics.autoconfigure.export.prometheus.PrometheusMetricsExportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableConfigurationProperties({PrometheusProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.prometheus",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
@AutoConfiguration(
   after = {PrometheusMetricsExportAutoConfiguration.class}
)
public class PrometheusAutoConfiguration {
   @Value("${spring.application.name}")
   private String applicationName;

   public PrometheusAutoConfiguration() {
   }

   @Bean
   @ConditionalOnBean({PrometheusMeterRegistry.class})
   public PrometheusCollector prometheusCollector(PrometheusMeterRegistry prometheusMeterRegistry) {
      return new PrometheusCollector(prometheusMeterRegistry);
   }

   @Bean({"prometheusThreadPoolTaskScheduler"})
   public ThreadPoolTaskScheduler prometheusThreadPoolTaskScheduler() {
      ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
      executor.setPoolSize(5);
      executor.setThreadGroupName("kmc-prometheus-task-scheduler");
      executor.setAwaitTerminationSeconds(60);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }

   @Bean
   MeterRegistryCustomizer<MeterRegistry> appMetricsCommonTags() {
      if (this.applicationName == null) {
         this.applicationName = PropertyUtils.getProperty("spring.application.name");
      }

      return (registry) -> registry.config().commonTags(new String[]{"application", this.applicationName}).meterFilter(new MeterFilter() {
            {
               Objects.requireNonNull(PrometheusAutoConfiguration.this);
            }

            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
               return id.getType() == Type.TIMER && id.getName().matches("^(jvm|http){1}.*") ? DistributionStatisticConfig.builder().percentilesHistogram(true).percentiles(new double[]{(double)0.5F, 0.9, 0.95, 0.99}).serviceLevelObjectives(new double[]{(double)Duration.ofMillis(50L).toNanos(), (double)Duration.ofMillis(100L).toNanos(), (double)Duration.ofMillis(200L).toNanos(), (double)Duration.ofMillis(1L).toNanos(), (double)Duration.ofMillis(5L).toNanos()}).minimumExpectedValue((double)Duration.ofMillis(1L).toNanos()).maximumExpectedValue((double)Duration.ofMillis(5L).toNanos()).build().merge(config) : config;
            }
         });
   }
}
