package com.kuma.boot.sms.common.loadbalancer;

import java.util.List;
import java.util.Random;

public class HashLoadBalancer extends AbstractLoadBalancer {
   public HashLoadBalancer() {
   }

   public HashLoadBalancer(List targetList) {
      super(targetList);
   }

   protected Object choose0(List activeTargetList, Object chooseReferenceObject) {
      if (chooseReferenceObject == null) {
         Random random = new Random();
         TargetWrapper<T> wrapper = (TargetWrapper)activeTargetList.get(random.nextInt(activeTargetList.size()));
         return wrapper == null ? null : wrapper.getTarget();
      } else {
         int hashCode = chooseReferenceObject.hashCode();
         int size = activeTargetList.size();
         int position = hashCode % size;
         TargetWrapper<T> wrapper = (TargetWrapper)activeTargetList.get(position);
         return wrapper == null ? null : wrapper.getTarget();
      }
   }
}
