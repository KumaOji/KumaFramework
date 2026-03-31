package com.kuma.boot.prometheus.collector;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

public class PrometheusCollector implements InitializingBean {
   private final PrometheusMeterRegistry prometheusMeterRegistry;
   @Value("${spring.application.name}")
   private String applicationName;
   public Counter requestCounter;
   public Gauge inprogressRequests;
   public DistributionSummary requestLatencyHistogram;
   public DistributionSummary requestLatency;

   public PrometheusCollector(PrometheusMeterRegistry prometheusMeterRegistry) {
      this.prometheusMeterRegistry = prometheusMeterRegistry;
   }

   private void init() {
      this.requestCounter = Counter.builder(this.getName() + "_requests_total").description("请求总数数据").tags(new String[]{"service", "method", "code", "dd"}).register(this.prometheusMeterRegistry);
      this.inprogressRequests = Gauge.builder(this.getName() + "_http_inprogress_requests", () -> (double)0.0F).tags(new String[]{"path", "method"}).description("进行中的请求状态数据").register(this.prometheusMeterRegistry);
   }

   private String getName() {
      return this.applicationName.replaceAll("-", "_");
   }

   public void afterPropertiesSet() throws Exception {
      this.init();
   }
}
