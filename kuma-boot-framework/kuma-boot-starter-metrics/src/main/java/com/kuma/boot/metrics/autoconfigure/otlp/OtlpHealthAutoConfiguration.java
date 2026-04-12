package com.kuma.boot.metrics.autoconfigure.otlp;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.metrics.autoconfigure.properties.OtlpMetricsProperties;
import com.kuma.boot.monitor.Monitor;
import com.kuma.boot.monitor.autoconfigure.HealthAutoConfiguration;
import com.kuma.boot.monitor.collect.HealthCheckProvider;
import com.kuma.boot.monitor.model.Report;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.registry.otlp.OtlpMeterRegistry;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableConfigurationProperties({OtlpMetricsProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.otlp",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
@ConditionalOnClass({HealthCheckProvider.class})
@AutoConfiguration(
   after = {HealthAutoConfiguration.class, OtlpAutoConfiguration.class}
)
public class OtlpHealthAutoConfiguration implements InitializingBean, DisposableBean {
   private final HealthCheckProvider healthCheckProvider;
   private final OtlpMeterRegistry otlpMeterRegistry;
   public final ThreadPoolTaskScheduler otlpThreadPoolTaskScheduler;
   private final Map<String, Gauge> gaugeMap = new ConcurrentHashMap();

   public OtlpHealthAutoConfiguration(@Autowired(required = false) HealthCheckProvider healthCheckProvider, OtlpMeterRegistry otlpMeterRegistry, @Autowired @Qualifier("otlpThreadPoolTaskScheduler") ThreadPoolTaskScheduler otlpThreadPoolTaskScheduler) {
      this.healthCheckProvider = healthCheckProvider;
      this.otlpMeterRegistry = otlpMeterRegistry;
      this.otlpThreadPoolTaskScheduler = otlpThreadPoolTaskScheduler;
   }

   public void destroy() throws Exception {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(OtlpHealthAutoConfiguration.class, "kuma-boot-starter-prometheus", new String[0]);
      if (Objects.nonNull(this.healthCheckProvider)) {
         Monitor monitorThreadPool = this.healthCheckProvider.getMonitor();
         this.otlpThreadPoolTaskScheduler.scheduleAtFixedRate(() -> {
            if (!monitorThreadPool.monitorIsShutdown()) {
               try {
                  Report report = this.healthCheckProvider.getReport(false);
                  report.eachReport((field, reportItem) -> {
                     Object value = reportItem.getValue();
                     String desc = reportItem.getDesc();
                     if (!StrUtil.isBlankIfStr(value) && value instanceof Number number) {
                        String[] fields = field.split("\\.");
                        List<String> list = ListUtil.of(fields).stream().distinct().toList();
                        Gauge gauge = (Gauge)this.gaugeMap.get(field);
                        if (Objects.isNull(gauge)) {
                           Objects.requireNonNull(number);
                           gauge = Gauge.builder(field, number::doubleValue).description(desc).register(this.otlpMeterRegistry);
                           this.gaugeMap.put(field, gauge);
                        }
                     }

                     return null;
                  });
                  LogUtils.debug("prometheusThreadPoolTaskScheduler , {}, \u65f6\u95f4\uff1a{}", new Object[]{Thread.currentThread().getName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))});
               } catch (Exception e) {
                  LogUtils.warn("kuma-boot-starter-metrics", new Object[]{"HealthCheck Prometheus error ", e});
               }
            }

         }, Duration.ofMillis(60000L));
      }

   }
}
