package com.kuma.boot.data.mongodb.helper.utils;

import cn.hutool.system.SystemUtil;

public class SystemTool {
   public SystemTool() {
   }

   public static String getSystem() {
      if (SystemUtil.get("os.name").toLowerCase().contains("windows")) {
         return "Windows";
      } else {
         return SystemUtil.get("os.name").toLowerCase().contains("mac os") ? "Mac OS" : "Linux";
      }
   }

   public static Boolean isWindows() {
      return getSystem().equals("Windows");
   }

   public static Boolean isMacOS() {
      return getSystem().equals("Mac OS");
   }

   public static Boolean isLinux() {
      return getSystem().equals("Linux");
   }

   public static boolean hasRoot() {
      if (isLinux()) {
         String user = System.getProperties().getProperty("user.name");
         return "root".equals(user);
      } else {
         return true;
      }
   }

   public static Class<?> deduceMainApplicationClass() {
      try {
         StackTraceElement[] stackTrace = (new RuntimeException()).getStackTrace();

         for(StackTraceElement stackTraceElement : stackTrace) {
            if ("main".equals(stackTraceElement.getMethodName())) {
               return Class.forName(stackTraceElement.getClassName());
            }
         }
      } catch (ClassNotFoundException var5) {
      }

      return null;
   }

   public static String deduceMainApplicationClassName() {
      Class<?> aClass = deduceMainApplicationClass();

      assert aClass != null;

      return aClass.getName();
   }
}
