package com.kuma.boot.prometheus.api.pojo;

import java.util.List;
import java.util.Map;

public class TargetGroup {
   private final List<String> targets;
   private final Map<String, String> labels;

   public TargetGroup(List<String> targets, Map<String, String> labels) {
      this.targets = targets;
      this.labels = labels;
   }

   public List<String> getTargets() {
      return this.targets;
   }

   public Map<String, String> getLabels() {
      return this.labels;
   }
}
