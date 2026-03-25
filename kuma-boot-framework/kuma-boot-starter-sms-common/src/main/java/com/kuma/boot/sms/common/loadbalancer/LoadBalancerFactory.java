package com.kuma.boot.sms.common.loadbalancer;

import java.util.List;

public class LoadBalancerFactory {
   public static ILoadBalancer build(String rule) {
      return build(rule, (List)null);
   }

   public static ILoadBalancer build(String rule, List targetList) {
      String instanceClassName = "net.guerlab.loadbalancer." + rule + "LoadBalancer";

      try {
         Class<?> clazz = LoadBalancerFactory.class.getClassLoader().loadClass(instanceClassName);
         return targetList == null ? (ILoadBalancer)clazz.getConstructor().newInstance() : (ILoadBalancer)clazz.getConstructor(List.class).newInstance(targetList);
      } catch (Exception e) {
         throw new RuntimeException(e.getLocalizedMessage(), e);
      }
   }
}
