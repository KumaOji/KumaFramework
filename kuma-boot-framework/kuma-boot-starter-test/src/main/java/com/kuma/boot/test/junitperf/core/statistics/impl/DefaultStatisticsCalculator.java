package com.kuma.boot.test.junitperf.core.statistics.impl;

import com.kuma.boot.test.junitperf.core.statistics.StatisticsCalculator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL,
   since = "2_0_0"
)
public class DefaultStatisticsCalculator implements StatisticsCalculator {
   private static final long serialVersionUID = 3715867392352544936L;
   private final DescriptiveStatistics latencyStatistics;
   private final AtomicLong evaluationCount;
   private final AtomicLong errorCount;
   private volatile long memoryKb;

   public DefaultStatisticsCalculator() {
      this(new SynchronizedDescriptiveStatistics());
   }

   private DefaultStatisticsCalculator(final DescriptiveStatistics latencyStatistics) {
      this.evaluationCount = new AtomicLong();
      this.errorCount = new AtomicLong();
      this.latencyStatistics = latencyStatistics;
   }

   public void addLatencyMeasurement(long executionTimeNs) {
      this.latencyStatistics.addValue((double)executionTimeNs);
   }

   public void incrementErrorCount() {
      this.errorCount.incrementAndGet();
   }

   public long getErrorCount() {
      return this.errorCount.get();
   }

   public float getErrorPercentage() {
      return (float)this.getErrorCount() / (float)this.getEvaluationCount() * 100.0F;
   }

   public void incrementEvaluationCount() {
      this.evaluationCount.incrementAndGet();
   }

   public long getEvaluationCount() {
      return this.evaluationCount.get();
   }

   public float getLatencyPercentile(int percentile, TimeUnit unit) {
      return (float)this.latencyStatistics.getPercentile((double)percentile) / (float)unit.toNanos(1L);
   }

   public float getMaxLatency(TimeUnit unit) {
      return (float)this.latencyStatistics.getMax() / (float)unit.toNanos(1L);
   }

   public float getMinLatency(TimeUnit unit) {
      return (float)this.latencyStatistics.getMin() / (float)unit.toNanos(1L);
   }

   public float getMeanLatency(TimeUnit unit) {
      return (float)this.latencyStatistics.getMean() / (float)unit.toNanos(1L);
   }

   public void setMemory(long memoryKb) {
      this.memoryKb = memoryKb;
   }

   public long getMemory() {
      return this.memoryKb;
   }
}
