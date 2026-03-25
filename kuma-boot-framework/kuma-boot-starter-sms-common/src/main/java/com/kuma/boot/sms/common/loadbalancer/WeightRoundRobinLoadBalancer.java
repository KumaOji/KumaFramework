package com.kuma.boot.sms.common.loadbalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WeightRoundRobinLoadBalancer extends AbstractLoadBalancer {
   private final Map weightMap = new ConcurrentHashMap();
   private final Lock lock = new ReentrantLock();
   private int position = 0;

   public WeightRoundRobinLoadBalancer() {
   }

   public WeightRoundRobinLoadBalancer(List targetList) {
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

      int size = targetList.size();
      this.lock.lock();

      Object var13;
      try {
         if (this.position >= size) {
            this.position = 0;
         }

         TargetWrapper<T> wrapper = (TargetWrapper)targetList.get(this.position);
         ++this.position;
         var13 = wrapper == null ? null : wrapper.getTarget();
      } finally {
         this.lock.unlock();
      }

      return var13;
   }
}
