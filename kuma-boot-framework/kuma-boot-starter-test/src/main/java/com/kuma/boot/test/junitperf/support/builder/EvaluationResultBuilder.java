package com.kuma.boot.test.junitperf.support.builder;

import com.google.common.collect.Maps;
import com.kuma.boot.test.junitperf.core.statistics.StatisticsCalculator;
import com.kuma.boot.test.junitperf.model.evaluation.component.EvaluationConfig;
import com.kuma.boot.test.junitperf.model.evaluation.component.EvaluationRequire;
import com.kuma.boot.test.junitperf.model.evaluation.component.EvaluationResult;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL
)
public class EvaluationResultBuilder {
   private final EvaluationConfig evaluationConfig;
   private final EvaluationRequire evaluationRequire;
   private final StatisticsCalculator statisticsCalculator;

   public EvaluationResultBuilder(EvaluationConfig evaluationConfig, EvaluationRequire evaluationRequire, StatisticsCalculator statisticsCalculator) {
      this.evaluationConfig = evaluationConfig;
      this.evaluationRequire = evaluationRequire;
      this.statisticsCalculator = statisticsCalculator;
   }

   public EvaluationResult build() {
      EvaluationResult evaluationResult = new EvaluationResult();
      evaluationResult.setMinAchieved(this.isMinAchieved());
      evaluationResult.setMaxAchieved(this.isMaxAchieved());
      evaluationResult.setAverageAchieved(this.isAverageAchieved());
      evaluationResult.setTimesPerSecondAchieved(this.isTimesPerSecondAchieved());
      Map<Integer, Boolean> isPercentilesAchievedMap = this.buildIsPercentilesAchievedMap();
      evaluationResult.setIsPercentilesAchievedMap(isPercentilesAchievedMap);
      evaluationResult.setPercentilesAchieved(this.isPercentilesAchieved(isPercentilesAchievedMap));
      evaluationResult.setSuccessful(this.isSuccessful(evaluationResult));
      evaluationResult.setThroughputQps(this.getThroughputQps());
      return evaluationResult;
   }

   public long getThroughputQps() {
      long configDuration = this.evaluationConfig.getConfigDuration();
      long configWarmUp = this.evaluationConfig.getConfigWarmUp();
      return (long)((float)this.statisticsCalculator.getEvaluationCount() / ((float)configDuration - (float)configWarmUp) * 1000.0F);
   }

   private boolean validateLatency(float actualNs, float requiredMs) {
      long thresholdNs = (long)(requiredMs * (float)TimeUnit.MILLISECONDS.toNanos(1L));
      return actualNs <= (float)thresholdNs;
   }

   public boolean isMinAchieved() {
      return this.evaluationRequire.getRequireMin() < 0.0F ? true : this.validateLatency(this.statisticsCalculator.getMinLatency(TimeUnit.NANOSECONDS), this.evaluationRequire.getRequireMin());
   }

   public boolean isMaxAchieved() {
      return this.evaluationRequire.getRequireMax() < 0.0F ? true : this.validateLatency(this.statisticsCalculator.getMaxLatency(TimeUnit.NANOSECONDS), this.evaluationRequire.getRequireMax());
   }

   public boolean isAverageAchieved() {
      return this.evaluationRequire.getRequireAverage() < 0.0F ? true : this.validateLatency(this.statisticsCalculator.getMeanLatency(TimeUnit.NANOSECONDS), this.evaluationRequire.getRequireAverage());
   }

   public boolean isTimesPerSecondAchieved() {
      return this.evaluationRequire.getRequireTimesPerSecond() < 0 || this.getThroughputQps() >= (long)this.evaluationRequire.getRequireTimesPerSecond();
   }

   private Map<Integer, Boolean> buildIsPercentilesAchievedMap() {
      Map<Integer, Boolean> isPercentilesAchievedMap = Maps.newTreeMap();

      for(Map.Entry<Integer, Float> entry : this.evaluationRequire.getRequirePercentilesMap().entrySet()) {
         Integer percentile = (Integer)entry.getKey();
         float thresholdMs = (Float)entry.getValue();
         long thresholdNs = (long)(thresholdMs * (float)TimeUnit.MILLISECONDS.toNanos(1L));
         boolean result = this.statisticsCalculator.getLatencyPercentile(percentile, TimeUnit.NANOSECONDS) <= (float)thresholdNs;
         isPercentilesAchievedMap.put(percentile, result);
      }

      return isPercentilesAchievedMap;
   }

   private boolean isPercentilesAchieved(Map<Integer, Boolean> isPercentilesAchievedMap) {
      for(Boolean bool : isPercentilesAchievedMap.values()) {
         if (!bool) {
            return false;
         }
      }

      return true;
   }

   public boolean isSuccessful(EvaluationResult evaluationResult) {
      return evaluationResult.isMaxAchieved() && evaluationResult.isMinAchieved() && evaluationResult.isAverageAchieved() && evaluationResult.isTimesPerSecondAchieved() && evaluationResult.isPercentilesAchieved();
   }
}
