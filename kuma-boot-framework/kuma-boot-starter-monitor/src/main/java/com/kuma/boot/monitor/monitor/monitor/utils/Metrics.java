package com.kuma.boot.monitor.monitor.monitor.utils;

import cn.hutool.core.util.StrUtil;
import org.springframework.context.ApplicationContext;

public class Metrics {
   public Metrics() {
   }

   public static String getApplicationName(ApplicationContext applicationContext) {
      if (applicationContext != null) {
         String name = applicationContext.getEnvironment().getProperty("spring.application.name");
         if (!StrUtil.isEmpty(name)) {
            return name;
         }

         name = applicationContext.getApplicationName();
         if (!StrUtil.isEmpty(name)) {
            return name;
         }
      }

      String name = System.getenv("SUPERVISOR_PROCESS_NAME");
      if (!StrUtil.isEmpty(name)) {
         return name;
      } else {
         name = System.getProperty("java.class.path");
         if (!StrUtil.isEmpty(name)) {
            return name;
         } else {
            name = System.getProperty("user.dir");
            name = name.substring(name.lastIndexOf("/"));
            return !StrUtil.isEmpty(name) ? name : "unknown";
         }
      }
   }
}
