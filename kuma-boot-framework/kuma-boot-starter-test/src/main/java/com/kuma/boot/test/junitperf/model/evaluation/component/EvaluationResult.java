package com.kuma.boot.test.junitperf.model.evaluation.component;

import com.kuma.boot.test.junitperf.model.BaseModel;
import java.util.Map;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL,
   since = "2_0_0"
)
public class EvaluationResult extends BaseModel {
   private static final long serialVersionUID = 3402389144055056153L;
   private long throughputQps;
   private boolean isMinAchieved;
   private boolean isMaxAchieved;
   private boolean isAverageAchieved;
   private boolean isTimesPerSecondAchieved;
   private boolean isPercentilesAchieved;
   private Map<Integer, Boolean> isPercentilesAchievedMap;
   private boolean isSuccessful;

   public EvaluationResult() {
   }

   public long getThroughputQps() {
      return this.throughputQps;
   }

   public void setThroughputQps(long throughputQps) {
      this.throughputQps = throughputQps;
   }

   public boolean isMinAchieved() {
      return this.isMinAchieved;
   }

   public void setMinAchieved(boolean minAchieved) {
      this.isMinAchieved = minAchieved;
   }

   public boolean isMaxAchieved() {
      return this.isMaxAchieved;
   }

   public void setMaxAchieved(boolean maxAchieved) {
      this.isMaxAchieved = maxAchieved;
   }

   public boolean isAverageAchieved() {
      return this.isAverageAchieved;
   }

   public void setAverageAchieved(boolean averageAchieved) {
      this.isAverageAchieved = averageAchieved;
   }

   public boolean isTimesPerSecondAchieved() {
      return this.isTimesPerSecondAchieved;
   }

   public void setTimesPerSecondAchieved(boolean timesPerSecondAchieved) {
      this.isTimesPerSecondAchieved = timesPerSecondAchieved;
   }

   public boolean isPercentilesAchieved() {
      return this.isPercentilesAchieved;
   }

   public void setPercentilesAchieved(boolean percentilesAchieved) {
      this.isPercentilesAchieved = percentilesAchieved;
   }

   public boolean isSuccessful() {
      return this.isSuccessful;
   }

   public void setSuccessful(boolean successful) {
      this.isSuccessful = successful;
   }

   public Map<Integer, Boolean> getIsPercentilesAchievedMap() {
      return this.isPercentilesAchievedMap;
   }

   public void setIsPercentilesAchievedMap(Map<Integer, Boolean> isPercentilesAchievedMap) {
      this.isPercentilesAchievedMap = isPercentilesAchievedMap;
   }
}
