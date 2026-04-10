package com.kuma.boot.test.junitperf.support.task;

import com.kuma.boot.test.junitperf.core.statistics.StatisticsCalculator;
import java.lang.reflect.Method;
import org.apache.lucene.util.RamUsageEstimator;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL
)
public class PerformanceEvaluationTask implements Runnable {
   private final long warmUpNs;
   private final StatisticsCalculator statisticsCalculator;
   private volatile boolean isContinue;
   private final Object testInstance;
   private final Method testMethod;

   public PerformanceEvaluationTask(long warmUpNs, StatisticsCalculator statisticsCalculator, Object testInstance, Method testMethod) {
      this.warmUpNs = warmUpNs;
      this.statisticsCalculator = statisticsCalculator;
      this.testInstance = testInstance;
      this.testMethod = testMethod;
      this.isContinue = true;
   }

   public void run() {
      long startTimeNs = System.nanoTime();
      long startMeasurements = startTimeNs + this.warmUpNs;
      long memoryKb = RamUsageEstimator.shallowSizeOf(this.testInstance);
      this.statisticsCalculator.setMemory(memoryKb);

      while(this.isContinue) {
         this.evaluateStatement(startMeasurements);
      }

   }

   private void evaluateStatement(long startMeasurements) {
      if (this.isContinue) {
         if (System.nanoTime() < startMeasurements) {
            try {
               this.testMethod.invoke(this.testInstance);
            } catch (Exception var7) {
            }
         } else {
            long startTimeNs = System.nanoTime();

            try {
               this.testMethod.invoke(this.testInstance);
               this.commonStatisticsUpdate(startTimeNs);
            } catch (Exception var6) {
               this.statisticsCalculator.incrementErrorCount();
               this.commonStatisticsUpdate(startTimeNs);
            }
         }

      }
   }

   private void commonStatisticsUpdate(final long startTimeNs) {
      this.statisticsCalculator.incrementEvaluationCount();
      this.statisticsCalculator.addLatencyMeasurement(this.getCostTimeNs(startTimeNs));
   }

   private long getCostTimeNs(long startTimeNs) {
      long currentTimeNs = System.nanoTime();
      return currentTimeNs - startTimeNs;
   }

   public boolean isContinue() {
      return this.isContinue;
   }

   public void setContinue(boolean aContinue) {
      this.isContinue = aContinue;
   }
}
