package com.kuma.boot.test.junitperf.model.evaluation.component;

import com.kuma.boot.test.junitperf.model.BaseModel;
import java.util.Map;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL,
   since = "2_0_0"
)
public class EvaluationRequire extends BaseModel {
   private static final long serialVersionUID = 377391606016334079L;
   private float requireMin;
   private float requireMax;
   private float requireAverage;
   private int requireTimesPerSecond;
   private Map<Integer, Float> requirePercentilesMap;

   public EvaluationRequire() {
   }

   public float getRequireMin() {
      return this.requireMin;
   }

   public void setRequireMin(float requireMin) {
      this.requireMin = requireMin;
   }

   public float getRequireMax() {
      return this.requireMax;
   }

   public void setRequireMax(float requireMax) {
      this.requireMax = requireMax;
   }

   public float getRequireAverage() {
      return this.requireAverage;
   }

   public void setRequireAverage(float requireAverage) {
      this.requireAverage = requireAverage;
   }

   public int getRequireTimesPerSecond() {
      return this.requireTimesPerSecond;
   }

   public void setRequireTimesPerSecond(int requireTimesPerSecond) {
      this.requireTimesPerSecond = requireTimesPerSecond;
   }

   public Map<Integer, Float> getRequirePercentilesMap() {
      return this.requirePercentilesMap;
   }

   public void setRequirePercentilesMap(Map<Integer, Float> requirePercentilesMap) {
      this.requirePercentilesMap = requirePercentilesMap;
   }
}
