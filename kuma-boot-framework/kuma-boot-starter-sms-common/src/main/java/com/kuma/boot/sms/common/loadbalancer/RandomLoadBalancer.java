package com.kuma.boot.sms.common.loadbalancer;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer extends AbstractLoadBalancer {
   public RandomLoadBalancer() {
   }

   public RandomLoadBalancer(List targetList) {
      super(targetList);
   }

   protected Object choose0(List activeTargetList, Object chooseReferenceObject) {
      TargetWrapper<T> wrapper = (TargetWrapper)activeTargetList.get((new Random()).nextInt(activeTargetList.size()));
      return wrapper == null ? null : wrapper.getTarget();
   }
}
