package com.kuma.boot.sms.common.loadbalancer;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RoundRobinLoadBalancer extends AbstractLoadBalancer {
   private final Lock lock = new ReentrantLock();
   private int position = 0;

   public RoundRobinLoadBalancer() {
   }

   public RoundRobinLoadBalancer(List targetList) {
      super(targetList);
   }

   protected Object choose0(List activeTargetList, Object chooseReferenceObject) {
      int size = activeTargetList.size();
      this.lock.lock();

      Object var5;
      try {
         if (this.position >= size) {
            this.position = 0;
         }

         TargetWrapper<T> wrapper = (TargetWrapper)activeTargetList.get(this.position);
         ++this.position;
         var5 = wrapper == null ? null : wrapper.getTarget();
      } finally {
         this.lock.unlock();
      }

      return var5;
   }
}
