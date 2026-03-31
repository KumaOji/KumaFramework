package com.kuma.boot.prometheus.autoconfigure;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.Monitor;
import com.kuma.boot.monitor.autoconfigure.HealthAutoConfiguration;
import com.kuma.boot.monitor.collect.HealthCheckProvider;
import com.kuma.boot.monitor.model.Report;
import com.kuma.boot.prometheus.autoconfigure.properties.PrometheusProperties;
import com.kuma.boot.prometheus.collector.PrometheusCollector;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableConfigurationProperties({PrometheusProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.prometheus",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
@ConditionalOnBean({PrometheusCollector.class})
@AutoConfiguration(
   after = {HealthAutoConfiguration.class, PrometheusAutoConfiguration.class}
)
public class PrometheusHealthAutoConfiguration implements InitializingBean, DisposableBean {
   private final HealthCheckProvider healthCheckProvider;
   private final PrometheusMeterRegistry prometheusMeterRegistry;
   public final ThreadPoolTaskScheduler prometheusThreadPoolTaskScheduler;
   private final Map<String, Gauge> gaugeMap = new ConcurrentHashMap();

   public PrometheusHealthAutoConfiguration(@Autowired(required = false) HealthCheckProvider healthCheckProvider, PrometheusMeterRegistry prometheusMeterRegistry, @Autowired @Qualifier("prometheusThreadPoolTaskScheduler") ThreadPoolTaskScheduler prometheusThreadPoolTaskScheduler) {
      this.healthCheckProvider = healthCheckProvider;
      this.prometheusMeterRegistry = prometheusMeterRegistry;
      this.prometheusThreadPoolTaskScheduler = prometheusThreadPoolTaskScheduler;
   }

   public void destroy() throws Exception {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(PrometheusHealthAutoConfiguration.class, "kuma-boot-starter-prometheus", new String[0]);
      if (Objects.nonNull(this.healthCheckProvider)) {
         Monitor monitorThreadPool = this.healthCheckProvider.getMonitor();
         this.prometheusThreadPoolTaskScheduler.scheduleAtFixedRate(() -> {
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
                           System.out.println("------" + field);
                           Objects.requireNonNull(number);
                           gauge = Gauge.builder(field, number::doubleValue).description(desc).register(this.prometheusMeterRegistry);
                           this.gaugeMap.put(field, gauge);
                        }
                     }

                     return null;
                  });
                  LogUtils.debug("prometheusThreadPoolTaskScheduler , {}, \u65f6\u95f4\uff1a{}", new Object[]{Thread.currentThread().getName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))});
               } catch (Exception e) {
                  LogUtils.warn("kuma-boot-starter-monitor", new Object[]{"HealthCheck Prometheus error ", e});
               }
            }

         }, Duration.ofMillis(60000L));
      }

   }
}
