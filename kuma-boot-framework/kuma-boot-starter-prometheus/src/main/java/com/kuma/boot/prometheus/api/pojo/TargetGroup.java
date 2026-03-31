package com.kuma.boot.prometheus.api.pojo;

import java.util.List;
import java.util.Map;

public class TargetGroup {
   private final List targets;
   private final Map labels;

   public TargetGroup(List targets, Map labels) {
      this.targets = targets;
      this.labels = labels;
   }

   public List getTargets() {
      return this.targets;
   }

   public Map getLabels() {
      return this.labels;
   }
}
