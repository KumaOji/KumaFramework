package com.kuma.boot.sms.common.loadbalancer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SmoothWeightRoundRobinLoadBalancer<T> extends AbstractLoadBalancer<T> {
   private final Map<T, Integer> originWeightMap = new ConcurrentHashMap<>();
   private final Map<T, Integer> currentWeightMap = new ConcurrentHashMap<>();
   private final Lock lock = new ReentrantLock();

   public SmoothWeightRoundRobinLoadBalancer() {
   }

   public SmoothWeightRoundRobinLoadBalancer(List<TargetWrapper<T>> targetList) {
      super(targetList);
   }

   protected void afterAdd(TargetWrapper<T> wrapper) {
      this.originWeightMap.put(wrapper.getTarget(), 1);
      this.currentWeightMap.put(wrapper.getTarget(), 1);
   }

   protected void afterRemove(TargetWrapper<T> wrapper) {
      this.originWeightMap.remove(wrapper.getTarget());
      this.currentWeightMap.remove(wrapper.getTarget());
   }

   public void setWeight(T target, int weight) {
      if (target != null) {
         this.originWeightMap.put(target, Math.max(weight, 1));
      }
   }

   protected T choose0(List<TargetWrapper<T>> activeTargetList, Object chooseReferenceObject) {
      this.lock.lock();

      try {
         int originWeightSum = 0;
         int currentMaxWeight = Integer.MIN_VALUE;
         TargetWrapper<T> currentMaxWeightWrapper = null;

         for (TargetWrapper<T> wrapper : activeTargetList) {
            int originWeight = this.originWeightMap.getOrDefault(wrapper.getTarget(), 1);
            int currentWeight = this.currentWeightMap.getOrDefault(wrapper.getTarget(), originWeight);
            originWeightSum += originWeight;
            if (currentWeight > currentMaxWeight) {
               currentMaxWeight = currentWeight;
               currentMaxWeightWrapper = wrapper;
            }
         }

         if (currentMaxWeightWrapper != null) {
            this.currentWeightMap.put(currentMaxWeightWrapper.getTarget(), currentMaxWeight - originWeightSum);

            for (TargetWrapper<T> wrapper : activeTargetList) {
               int originWeight = this.originWeightMap.getOrDefault(wrapper.getTarget(), 1);
               int currentWeight = this.currentWeightMap.getOrDefault(wrapper.getTarget(), 1);
               this.currentWeightMap.put(wrapper.getTarget(), currentWeight + originWeight);
            }

            return currentMaxWeightWrapper.getTarget();
         }

         return null;
      } finally {
         this.lock.unlock();
      }
   }
}
