package com.kuma.boot.sms.common.loadbalancer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SmoothWeightRoundRobinLoadBalancer extends AbstractLoadBalancer {
   private final Map originWeightMap = new ConcurrentHashMap();
   private final Map currentWeightMap = new ConcurrentHashMap();
   private final Lock lock = new ReentrantLock();

   public SmoothWeightRoundRobinLoadBalancer() {
   }

   public SmoothWeightRoundRobinLoadBalancer(List targetList) {
      super(targetList);
   }

   protected void afterAdd(TargetWrapper wrapper) {
      this.originWeightMap.put(wrapper.getTarget(), 1);
      this.currentWeightMap.put(wrapper.getTarget(), 1);
   }

   protected void afterRemove(TargetWrapper wrapper) {
      this.originWeightMap.remove(wrapper.getTarget());
      this.currentWeightMap.remove(wrapper.getTarget());
   }

   public void setWeight(Object target, int weight) {
      if (target != null) {
         this.originWeightMap.put(target, Math.max(weight, 1));
      }
   }

   protected Object choose0(List activeTargetList, Object chooseReferenceObject) {
      this.lock.lock();

      Object var13;
      try {
         int originWeightSum = 0;
         int currentMaxWeight = Integer.MIN_VALUE;
         TargetWrapper<T> currentMaxWeightWrapper = null;

         for(TargetWrapper wrapper : activeTargetList) {
            int originWeight = (Integer)this.originWeightMap.getOrDefault(wrapper.getTarget(), 1);
            int currentWeight = (Integer)this.currentWeightMap.getOrDefault(wrapper.getTarget(), originWeight);
            originWeightSum += originWeight;
            if (currentWeight > currentMaxWeight) {
               currentMaxWeight = currentWeight;
               currentMaxWeightWrapper = wrapper;
            }
         }

         if (currentMaxWeightWrapper != null) {
            this.currentWeightMap.put(currentMaxWeightWrapper.getTarget(), currentMaxWeight - originWeightSum);

            for(TargetWrapper wrapper : activeTargetList) {
               int originWeight = (Integer)this.originWeightMap.getOrDefault(wrapper.getTarget(), 1);
               int currentWeight = (Integer)this.currentWeightMap.getOrDefault(wrapper.getTarget(), 1);
               this.currentWeightMap.put(wrapper.getTarget(), currentWeight + originWeight);
            }

            var13 = currentMaxWeightWrapper.getTarget();
            return var13;
         }

         var13 = null;
      } finally {
         this.lock.unlock();
      }

      return var13;
   }
}
