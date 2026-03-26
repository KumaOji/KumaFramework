package com.kuma.boot.sms.common.loadbalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class WeightRandomLoadBalancer<T> extends AbstractLoadBalancer<T> {
   private final Map<T, Integer> weightMap = new ConcurrentHashMap<>();

   public WeightRandomLoadBalancer() {
   }

   public WeightRandomLoadBalancer(List<TargetWrapper<T>> targetList) {
      super(targetList);
   }

   protected void afterAdd(TargetWrapper<T> wrapper) {
      this.weightMap.put(wrapper.getTarget(), 1);
   }

   protected void afterRemove(TargetWrapper<T> wrapper) {
      this.weightMap.remove(wrapper.getTarget());
   }

   public void setWeight(T target, int weight) {
      if (target != null) {
         this.weightMap.put(target, Math.max(weight, 1));
      }
   }

   protected T choose0(List<TargetWrapper<T>> activeTargetList, Object chooseReferenceObject) {
      List<TargetWrapper<T>> targetList = new ArrayList<>();

      for (TargetWrapper<T> wrapper : activeTargetList) {
         int weight = this.weightMap.getOrDefault(wrapper.getTarget(), 1);

         for (int i = 0; i < weight; ++i) {
            targetList.add(wrapper);
         }
      }

      TargetWrapper<T> wrapper = targetList.get((new Random()).nextInt(targetList.size()));
      return wrapper == null ? null : wrapper.getTarget();
   }
}
