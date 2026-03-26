package com.kuma.boot.sms.common.loadbalancer;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RoundRobinLoadBalancer<T> extends AbstractLoadBalancer<T> {
   private final Lock lock = new ReentrantLock();
   private int position = 0;

   public RoundRobinLoadBalancer() {
   }

   public RoundRobinLoadBalancer(List<TargetWrapper<T>> targetList) {
      super(targetList);
   }

   protected T choose0(List<TargetWrapper<T>> activeTargetList, Object chooseReferenceObject) {
      int size = activeTargetList.size();
      this.lock.lock();

      try {
         if (this.position >= size) {
            this.position = 0;
         }

         TargetWrapper<T> wrapper = activeTargetList.get(this.position);
         ++this.position;
         return wrapper == null ? null : wrapper.getTarget();
      } finally {
         this.lock.unlock();
      }
   }
}
