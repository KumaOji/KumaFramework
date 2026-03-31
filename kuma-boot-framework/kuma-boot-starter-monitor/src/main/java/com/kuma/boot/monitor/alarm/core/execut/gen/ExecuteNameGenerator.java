package com.kuma.boot.monitor.alarm.core.execut.gen;

public class ExecuteNameGenerator {
   public ExecuteNameGenerator() {
   }

   public static String genExecuteName(Class clz) {
      String simpleName = clz.getSimpleName();
      int index = simpleName.indexOf("Execute");
      return index > 0 ? simpleName.substring(0, index).toUpperCase() : simpleName;
   }
}
