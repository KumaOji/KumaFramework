package com.kuma.boot.test.junitperf.core.report.impl;

import com.kuma.boot.common.utils.lang.ConsoleUtils;
import com.kuma.boot.test.junitperf.constant.enums.StatusEnum;
import com.kuma.boot.test.junitperf.core.report.Reporter;
import com.kuma.boot.test.junitperf.core.statistics.StatisticsCalculator;
import com.kuma.boot.test.junitperf.model.evaluation.EvaluationContext;
import com.kuma.boot.test.junitperf.model.evaluation.component.EvaluationConfig;
import com.kuma.boot.test.junitperf.model.evaluation.component.EvaluationRequire;
import com.kuma.boot.test.junitperf.model.evaluation.component.EvaluationResult;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL,
   since = "2_0_0"
)
public class ConsoleReporter implements Reporter {
   public ConsoleReporter() {
   }

   public void report(Class testClass, Collection<EvaluationContext> evaluationContextSet) {
      for(EvaluationContext context : evaluationContextSet) {
         StatisticsCalculator statistics = context.getStatisticsCalculator();
         EvaluationConfig evaluationConfig = context.getEvaluationConfig();
         EvaluationRequire evaluationRequire = context.getEvaluationRequire();
         EvaluationResult evaluationResult = context.getEvaluationResult();
         String throughputStatus = this.getStatus(evaluationResult.isTimesPerSecondAchieved());
         this.infoLog(context, "--------------------------------------------------------");
         this.infoLog(context, "Started at:  {}", context.getStartTime());
         this.infoLog(context, "Invocations:  {}", statistics.getEvaluationCount());
         this.infoLog(context, "Success:  {}", statistics.getEvaluationCount() - statistics.getErrorCount());
         this.infoLog(context, "Errors:  {}", statistics.getErrorCount());
         this.infoLog(context, "Thread Count:  {}", evaluationConfig.getConfigThreads());
         this.infoLog(context, "Warm up:  {}ms", evaluationConfig.getConfigWarmUp());
         this.infoLog(context, "Execution time:  {}ms", evaluationConfig.getConfigDuration());
         this.infoLog(context, "Throughput:  {}/s (Required: {}/s) - {}", evaluationResult.getThroughputQps(), evaluationRequire.getRequireTimesPerSecond(), throughputStatus);
         this.infoLog(context, "Memory cost:  {}byte", statistics.getMemory());
         this.infoLog(context, "Min latency:  {}ms (Required: {}ms) - {}", statistics.getMinLatency(TimeUnit.MILLISECONDS), evaluationRequire.getRequireMin(), this.getStatus(evaluationResult.isMinAchieved()));
         this.infoLog(context, "Max latency:  {}ms (Required: {}ms) - {}", statistics.getMaxLatency(TimeUnit.MILLISECONDS), evaluationRequire.getRequireMax(), this.getStatus(evaluationResult.isMaxAchieved()));
         this.infoLog(context, "Avg latency:  {}ms (Required: {}ms) - {}", statistics.getMeanLatency(TimeUnit.MILLISECONDS), evaluationRequire.getRequireAverage(), this.getStatus(evaluationResult.isAverageAchieved()));

         for(Map.Entry<Integer, Float> entry : evaluationRequire.getRequirePercentilesMap().entrySet()) {
            Integer percentile = (Integer)entry.getKey();
            Float threshold = (Float)entry.getValue();
            boolean result = (Boolean)evaluationResult.getIsPercentilesAchievedMap().get(percentile);
            String percentileStatus = this.getStatus(result);
            this.infoLog(context, "Percentile: {}%%   {}ms (Required: {}ms) - {}", percentile, statistics.getLatencyPercentile(percentile, TimeUnit.MILLISECONDS), threshold, percentileStatus);
         }

         this.infoLog(context, "--------------------------------------------------------");
      }

   }

   private void infoLog(final EvaluationContext context, final String format, final Object... args) {
      String className = context.getTestInstance().getClass().getName();
      String methodName = context.getMethodName();
      ConsoleUtils.info(className, methodName, format, args);
   }

   private String getStatus(boolean isSuccess) {
      return isSuccess ? StatusEnum.PASSED.getStatus() : StatusEnum.FAILED.getStatus();
   }
}
