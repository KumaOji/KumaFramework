package com.kuma.boot.test.junitperf.model.evaluation.component;

import com.kuma.boot.test.junitperf.model.BaseModel;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL,
   since = "2_0_0"
)
public class EvaluationConfig extends BaseModel {
   private static final long serialVersionUID = 3584449169952751834L;
   private int configThreads;
   private long configWarmUp;
   private long configDuration;

   public EvaluationConfig() {
   }

   public int getConfigThreads() {
      return this.configThreads;
   }

   public void setConfigThreads(int configThreads) {
      this.configThreads = configThreads;
   }

   public long getConfigWarmUp() {
      return this.configWarmUp;
   }

   public void setConfigWarmUp(long configWarmUp) {
      this.configWarmUp = configWarmUp;
   }

   public long getConfigDuration() {
      return this.configDuration;
   }

   public void setConfigDuration(long configDuration) {
      this.configDuration = configDuration;
   }
}
