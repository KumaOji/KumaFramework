package com.kuma.boot.test.junitperf.model.evaluation;

import com.kuma.boot.test.junitperf.core.annotation.KmcTest;
import com.kuma.boot.test.junitperf.core.annotation.KmcTestRequire;
import com.kuma.boot.test.junitperf.core.statistics.StatisticsCalculator;
import com.kuma.boot.test.junitperf.model.evaluation.component.EvaluationConfig;
import com.kuma.boot.test.junitperf.model.evaluation.component.EvaluationRequire;
import com.kuma.boot.test.junitperf.model.evaluation.component.EvaluationResult;
import com.kuma.boot.test.junitperf.support.builder.EvaluationConfigBuilder;
import com.kuma.boot.test.junitperf.support.builder.EvaluationRequireBuilder;
import com.kuma.boot.test.junitperf.support.builder.EvaluationResultBuilder;
import java.io.Serializable;
import java.lang.reflect.Method;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL,
   since = "2_0_0"
)
public class EvaluationContext implements Serializable {
   private static final long serialVersionUID = -3314188451986878388L;
   private final Object testInstance;
   private final Method testMethod;
   private final String methodName;
   private final String startTime;
   private StatisticsCalculator statisticsCalculator;
   private EvaluationConfig evaluationConfig;
   private EvaluationRequire evaluationRequire;
   private EvaluationResult evaluationResult;

   public EvaluationContext(final Object testInstance, final Method testMethod, String startTime) {
      this.testInstance = testInstance;
      this.testMethod = testMethod;
      this.methodName = testMethod.getName();
      this.startTime = startTime;
   }

   public synchronized void loadConfig(KmcTest kmcTest) {
      this.evaluationConfig = (new EvaluationConfigBuilder(kmcTest)).build();
   }

   public synchronized void loadRequire(KmcTestRequire kmcTestRequire) {
      this.evaluationRequire = (new EvaluationRequireBuilder(kmcTestRequire)).build();
   }

   public void runValidation() {
      this.evaluationResult = (new EvaluationResultBuilder(this.evaluationConfig, this.evaluationRequire, this.statisticsCalculator)).build();
   }

   public String getMethodName() {
      return this.methodName;
   }

   public String getStartTime() {
      return this.startTime;
   }

   public StatisticsCalculator getStatisticsCalculator() {
      return this.statisticsCalculator;
   }

   public void setStatisticsCalculator(StatisticsCalculator statisticsCalculator) {
      this.statisticsCalculator = statisticsCalculator;
   }

   public EvaluationConfig getEvaluationConfig() {
      return this.evaluationConfig;
   }

   public EvaluationRequire getEvaluationRequire() {
      return this.evaluationRequire;
   }

   public EvaluationResult getEvaluationResult() {
      return this.evaluationResult;
   }

   public Object getTestInstance() {
      return this.testInstance;
   }

   public Method getTestMethod() {
      return this.testMethod;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         EvaluationContext that = (EvaluationContext)o;
         if (this.methodName != null) {
            if (!this.methodName.equals(that.methodName)) {
               return false;
            }
         } else if (that.methodName != null) {
            return false;
         }

         if (this.startTime != null) {
            if (!this.startTime.equals(that.startTime)) {
               return false;
            }
         } else if (that.startTime != null) {
            return false;
         }

         if (this.statisticsCalculator != null) {
            if (!this.statisticsCalculator.equals(that.statisticsCalculator)) {
               return false;
            }
         } else if (that.statisticsCalculator != null) {
            return false;
         }

         if (this.evaluationConfig != null) {
            if (!this.evaluationConfig.equals(that.evaluationConfig)) {
               return false;
            }
         } else if (that.evaluationConfig != null) {
            return false;
         }

         if (this.evaluationRequire != null) {
            if (!this.evaluationRequire.equals(that.evaluationRequire)) {
               return false;
            }
         } else if (that.evaluationRequire != null) {
            return false;
         }

         return this.evaluationResult != null ? this.evaluationResult.equals(that.evaluationResult) : that.evaluationResult == null;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.methodName != null ? this.methodName.hashCode() : 0;
      result = 31 * result + (this.startTime != null ? this.startTime.hashCode() : 0);
      result = 31 * result + (this.statisticsCalculator != null ? this.statisticsCalculator.hashCode() : 0);
      result = 31 * result + (this.evaluationConfig != null ? this.evaluationConfig.hashCode() : 0);
      result = 31 * result + (this.evaluationRequire != null ? this.evaluationRequire.hashCode() : 0);
      result = 31 * result + (this.evaluationResult != null ? this.evaluationResult.hashCode() : 0);
      return result;
   }
}
