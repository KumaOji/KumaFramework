package com.kuma.boot.test.junitperf.support.builder;

import com.google.common.base.Preconditions;
import com.kuma.boot.test.junitperf.core.annotation.KmcTest;
import com.kuma.boot.test.junitperf.model.evaluation.component.EvaluationConfig;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL
)
public class EvaluationConfigBuilder {
   private final KmcTest kmcTest;

   public EvaluationConfigBuilder(KmcTest kmcTest) {
      this.kmcTest = kmcTest;
   }

   public EvaluationConfig build() {
      this.validateJunitPerfConfig(this.kmcTest);
      EvaluationConfig evaluationConfig = new EvaluationConfig();
      evaluationConfig.setConfigThreads(this.kmcTest.threads());
      evaluationConfig.setConfigWarmUp(this.kmcTest.warmUp());
      evaluationConfig.setConfigDuration(this.kmcTest.duration());
      return evaluationConfig;
   }

   private void validateJunitPerfConfig(KmcTest kmcTest) {
      Preconditions.checkNotNull(kmcTest, "JunitPerfConfig must not be null!");
      int threads = kmcTest.threads();
      long warmUp = kmcTest.warmUp();
      long duration = kmcTest.duration();
      Preconditions.checkState(duration > 0L, "duration must be > 0ms.");
      Preconditions.checkState(warmUp >= 0L, "warmUp must be >= 0ms.");
      Preconditions.checkState(warmUp < duration, "warmUp must be < duration.");
      Preconditions.checkState(threads > 0, "threads must be > 0.");
   }
}
