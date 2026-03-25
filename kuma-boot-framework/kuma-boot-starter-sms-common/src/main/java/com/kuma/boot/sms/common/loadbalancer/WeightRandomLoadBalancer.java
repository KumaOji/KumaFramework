package com.kuma.boot.sms.common.loadbalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class WeightRandomLoadBalancer extends AbstractLoadBalancer {
   private final Map weightMap = new ConcurrentHashMap();

   public WeightRandomLoadBalancer() {
   }

   public WeightRandomLoadBalancer(List targetList) {
      super(targetList);
   }

   protected void afterAdd(TargetWrapper wrapper) {
      this.weightMap.put(wrapper.getTarget(), 1);
   }

   protected void afterRemove(TargetWrapper wrapper) {
      this.weightMap.remove(wrapper.getTarget());
   }

   public void setWeight(Object target, int weight) {
      if (target != null) {
         this.weightMap.put(target, Math.max(weight, 1));
      }
   }

   protected Object choose0(List activeTargetList, Object chooseReferenceObject) {
      List<TargetWrapper<T>> targetList = new ArrayList();

      for(TargetWrapper wrapper : activeTargetList) {
         int weight = (Integer)this.weightMap.getOrDefault(wrapper.getTarget(), 1);

         for(int i = 0; i < weight; ++i) {
            targetList.add(wrapper);
         }
      }

      TargetWrapper<T> wrapper = (TargetWrapper)targetList.get((new Random()).nextInt(targetList.size()));
      return wrapper == null ? null : wrapper.getTarget();
   }
}
