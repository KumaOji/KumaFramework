/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.data.mongodb.helper.utils;


import cn.hutool.system.SystemPropsKeys;
import cn.hutool.system.SystemUtil;

/**
 * SystemTool
 *
 * @author kuma
 * @version 2022.05
 * @since 2022-05-27 21:55:22
 */
public class SystemTool {

   /**
    * 得到系统
    *
    * @return {@link String }
    * @since 2022-05-27 21:55:22
    */
   public static String getSystem() {

      if (SystemUtil.get(SystemPropsKeys.OS_NAME).toLowerCase().contains("windows")) {
         return "Windows";
      } else if (SystemUtil.get(SystemPropsKeys.OS_NAME).toLowerCase().contains("mac os")) {
         return "Mac OS";
      } else {
         return "Linux";
      }
   }

   /**
    * 是窗户
    *
    * @return {@link Boolean }
    * @since 2022-05-27 21:55:22
    */
   public static Boolean isWindows() {
      return getSystem().equals("Windows");
   }

   /**
    * 是mac os
    *
    * @return {@link Boolean }
    * @since 2022-05-27 21:55:22
    */
   public static Boolean isMacOS() {
      return getSystem().equals("Mac OS");
   }

   /**
    * 是linux
    *
    * @return {@link Boolean }
    * @since 2022-05-27 21:55:22
    */
   public static Boolean isLinux() {
      return getSystem().equals("Linux");
   }

   /**
    * 有根
    *
    * @return boolean
    * @since 2022-05-27 21:55:22
    */
   public static boolean hasRoot() {
      if (SystemTool.isLinux()) {
         String user = System.getProperties().getProperty(SystemPropsKeys.USER_NAME);
         return "root".equals(user);
      }
      return true;
   }

   /**
    * 推断主要应用程序类
    *
    * @return {@link Class }<{@link ? }>
    * @since 2022-05-27 21:55:22
    */
   public static Class<?> deduceMainApplicationClass() {
      try {
         StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
         for (StackTraceElement stackTraceElement : stackTrace) {
            if ("main".equals(stackTraceElement.getMethodName())) {
               return Class.forName(stackTraceElement.getClassName());
            }
         }
      } catch (ClassNotFoundException ex) {
         // Swallow and continue
      }
      return null;
   }

   /**
    * 推断主应用程序类名
    *
    * @return {@link String }
    * @since 2022-05-27 21:55:22
    */
   public static String deduceMainApplicationClassName() {
      Class<?> aClass = deduceMainApplicationClass();
      assert aClass != null;
      return aClass.getName();
   }
}
