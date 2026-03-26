package com.kuma.boot.sms.common.loadbalancer;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer<T> extends AbstractLoadBalancer<T> {
   public RandomLoadBalancer() {
   }

   public RandomLoadBalancer(List<TargetWrapper<T>> targetList) {
      super(targetList);
   }

   protected T choose0(List<TargetWrapper<T>> activeTargetList, Object chooseReferenceObject) {
      TargetWrapper<T> wrapper = activeTargetList.get((new Random()).nextInt(activeTargetList.size()));
      return wrapper == null ? null : wrapper.getTarget();
   }
}
