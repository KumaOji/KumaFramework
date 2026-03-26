package com.kuma.boot.sms.common.loadbalancer;

import java.util.List;
import java.util.Random;

public class HashLoadBalancer<T> extends AbstractLoadBalancer<T> {
   public HashLoadBalancer() {
   }

   public HashLoadBalancer(List<TargetWrapper<T>> targetList) {
      super(targetList);
   }

   protected T choose0(List<TargetWrapper<T>> activeTargetList, Object chooseReferenceObject) {
      if (chooseReferenceObject == null) {
         Random random = new Random();
         TargetWrapper<T> wrapper = activeTargetList.get(random.nextInt(activeTargetList.size()));
         return wrapper == null ? null : wrapper.getTarget();
      } else {
         int hashCode = chooseReferenceObject.hashCode();
         int size = activeTargetList.size();
         int position = hashCode % size;
         TargetWrapper<T> wrapper = activeTargetList.get(position);
         return wrapper == null ? null : wrapper.getTarget();
      }
   }
}
