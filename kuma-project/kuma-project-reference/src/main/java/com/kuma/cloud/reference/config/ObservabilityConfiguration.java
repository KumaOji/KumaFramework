package com.kuma.cloud.reference.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.kuma.boot.auditlog.autoconfigure.AuditLogAutoConfiguration;
import com.kuma.boot.metrics.method.autoconfigure.MethodMetricsAutoConfiguration;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import java.util.List;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/** Reuses framework audit and method metrics; binds the nested pools of the routing datasource. */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({AuditLogAutoConfiguration.class, MethodMetricsAutoConfiguration.class})
public class ObservabilityConfiguration {
    @Bean
    SmartInitializingSingleton referencePoolMetrics(MeterRegistry registry, DynamicRoutingDataSource dataSource,
            @Qualifier("asyncThreadPoolTaskExecutor") ThreadPoolTaskExecutor executor) {
        return () -> {
            dataSource.getDataSources().forEach((name, source) -> {
                try {
                    HikariDataSource pool = source.unwrap(HikariDataSource.class);
                    if (pool.getMetricRegistry() == null && pool.getMetricsTrackerFactory() == null) {
                        pool.setMetricsTrackerFactory(new MicrometerMetricsTrackerFactory(registry));
                    }
                } catch (java.sql.SQLException error) {
                    throw new IllegalStateException("Cannot bind datasource metrics: " + name, error);
                }
            });
            new ExecutorServiceMetrics(executor.getThreadPoolExecutor(), "reference-business", List.of())
                    .bindTo(registry);
        };
    }
}
