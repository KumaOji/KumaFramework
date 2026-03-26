package com.kuma.boot.sms.common.loadbalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WeightRoundRobinLoadBalancer<T> extends AbstractLoadBalancer<T> {
   private final Map<T, Integer> weightMap = new ConcurrentHashMap<>();
   private final Lock lock = new ReentrantLock();
   private int position = 0;

   public WeightRoundRobinLoadBalancer() {
   }

   public WeightRoundRobinLoadBalancer(List<TargetWrapper<T>> targetList) {
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

      int size = targetList.size();
      this.lock.lock();

      try {
         if (this.position >= size) {
            this.position = 0;
         }

         TargetWrapper<T> wrapper = targetList.get(this.position);
         ++this.position;
         return wrapper == null ? null : wrapper.getTarget();
      } finally {
         this.lock.unlock();
      }
   }
}
