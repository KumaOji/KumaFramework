package com.kuma.boot.eventbus.disruptor.tmp4.util;

public class CglibUtils {
   public CglibUtils() {
   }

   public static Class<?> filterCglibProxyClass(Class<?> clazz) {
      while(isCglibProxyClass(clazz)) {
         clazz = clazz.getSuperclass();
      }

      return clazz;
   }

   public static boolean isCglibProxyClass(Class<?> clazz) {
      return clazz != null && clazz.getName().contains("$$");
   }
}
