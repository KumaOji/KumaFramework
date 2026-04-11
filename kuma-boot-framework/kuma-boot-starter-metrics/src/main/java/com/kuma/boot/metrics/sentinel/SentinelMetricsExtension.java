package com.taotao.boot.metrics.sentinel;

import com.alibaba.csp.sentinel.metric.extension.MetricExtension;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SentinelMetricsExtension implements MetricExtension {
   public static final String SENTINEL_METRIC_NAME_PREFIX = "undertow";
   public static final String PASS_REQUESTS_TOTAL = "undertow.pass.requests.total";
   public static final String BLOCK_REQUESTS_TOTAL = "undertow.block.requests.total";
   public static final String SUCCESS_REQUESTS_TOTAL = "undertow.success.requests.total";
   public static final String EXCEPTION_REQUESTS_TOTAL = "undertow.exception_requests_total";
   public static final String REQUESTS_LATENCY_SECONDS = "undertow.requests.latency.seconds";
   public static final String CURRENT_THREADS = "undertow.current.threads";
   public static final String DEFAULT_TAT_NAME = "resource";
   private final AtomicLong CURRENT_THREAD_COUNT = new AtomicLong(0L);

   public SentinelMetricsExtension() {
   }

   public void addPass(String resource, int n, Object... args) {
      Metrics.counter("undertow.pass.requests.total", new String[]{"resource", resource}).increment((double)n);
   }

   public void addBlock(String resource, int n, String origin, BlockException ex, Object... args) {
      Metrics.counter("undertow.block.requests.total", new String[]{resource, ex.getClass().getSimpleName(), ex.getRuleLimitApp(), origin}).increment((double)n);
   }

   public void addSuccess(String resource, int n, Object... args) {
      Metrics.counter("undertow.success.requests.total", new String[]{"resource", resource}).increment((double)n);
   }

   public void addException(String resource, int n, Throwable throwable) {
      Metrics.counter("undertow.exception_requests_total", new String[]{"resource", resource}).increment((double)n);
   }

   public void addRt(String resource, long rt, Object... args) {
      Metrics.timer("undertow.requests.latency.seconds", new String[]{"resource", resource}).record(rt, TimeUnit.MICROSECONDS);
   }

   public void increaseThreadNum(String resource, Object... args) {
      Tags tags = Tags.of("resource", resource);
      Metrics.gauge("undertow.current.threads", tags, this.CURRENT_THREAD_COUNT, AtomicLong::incrementAndGet);
   }

   public void decreaseThreadNum(String resource, Object... args) {
      Tags tags = Tags.of("resource", resource);
      Metrics.gauge("undertow.current.threads", tags, this.CURRENT_THREAD_COUNT, AtomicLong::decrementAndGet);
   }
}
