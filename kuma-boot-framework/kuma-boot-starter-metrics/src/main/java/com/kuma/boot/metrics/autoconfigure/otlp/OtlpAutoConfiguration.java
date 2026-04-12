package com.kuma.boot.metrics.autoconfigure.otlp;

import com.kuma.boot.metrics.autoconfigure.properties.OtlpMetricsProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.micrometer.metrics.autoconfigure.export.otlp.OtlpMetricsExportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableConfigurationProperties({OtlpMetricsProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.otlp",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
@AutoConfiguration(
   after = {OtlpMetricsExportAutoConfiguration.class}
)
public class OtlpAutoConfiguration {
   public OtlpAutoConfiguration() {
   }

   @Bean({"otlpThreadPoolTaskScheduler"})
   public ThreadPoolTaskScheduler otlpThreadPoolTaskScheduler() {
      ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
      executor.setPoolSize(5);
      executor.setThreadGroupName("kmc-otlp-task-scheduler");
      executor.setAwaitTerminationSeconds(60);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }
}
