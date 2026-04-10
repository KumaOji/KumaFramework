package com.kuma.boot.test.junitperf.core.statistics;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL
)
public interface StatisticsCalculator extends Serializable {
   void addLatencyMeasurement(long executionTimeNs);

   void incrementErrorCount();

   long getErrorCount();

   float getErrorPercentage();

   void incrementEvaluationCount();

   long getEvaluationCount();

   float getLatencyPercentile(int percentile, TimeUnit unit);

   float getMaxLatency(TimeUnit unit);

   float getMinLatency(TimeUnit unit);

   float getMeanLatency(TimeUnit unit);

   void setMemory(final long memoryKb);

   long getMemory();
}
