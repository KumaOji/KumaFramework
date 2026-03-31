package com.kuma.boot.prometheus.collector;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.distribution.TimeWindowPercentileHistogram;
import io.micrometer.prometheusmetrics.PrometheusDistributionSummary;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

public class PrometheusCollector implements InitializingBean {
   private final PrometheusMeterRegistry prometheusMeterRegistry;
   @Value("${spring.application.name}")
   private String applicationName;
   public Counter requestCounter;
   public Gauge inprogressRequests;
   public TimeWindowPercentileHistogram requestLatencyHistogram;
   public PrometheusDistributionSummary requestLatency;

   public PrometheusCollector(PrometheusMeterRegistry prometheusMeterRegistry) {
      this.prometheusMeterRegistry = prometheusMeterRegistry;
   }

   private void init() {
      this.requestCounter = Counter.builder(this.getName() + "_requests_total").description("\u8bf7\u6c42\u603b\u6570\u6570\u636e").tags(new String[]{"service", "method", "code", "dd"}).register(this.prometheusMeterRegistry);
      this.inprogressRequests = Gauge.builder(this.getName() + "_http_inprogress_requests", () -> (double)0.0F).tags(new String[]{"path", "method"}).description("\u8fdb\u884c\u4e2d\u7684\u8bf7\u6c42\u72b6\u6001\u6570\u636e").register(this.prometheusMeterRegistry);
   }

   private String getName() {
      return this.applicationName.replaceAll("-", "_");
   }

   public void afterPropertiesSet() throws Exception {
      this.init();
   }
}
