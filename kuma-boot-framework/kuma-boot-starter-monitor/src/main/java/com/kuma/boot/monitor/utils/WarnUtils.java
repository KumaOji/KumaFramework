package com.kuma.boot.monitor.utils;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.reflect.ReflectionUtils;

public class WarnUtils {
   public static final String ALARM_ERROR = "ERROR";
   public static final String ALARM_WARN = "WARN";
   public static final String ALARM_INFO = "INFO";

   public WarnUtils() {
   }

   public static void notifynow(String alarm_type, String title, String content) {
      notify(alarm_type, title, content, true);
   }

   public static void notify(String alarm_type, String title, String content) {
      notify(alarm_type, title, content, false);
   }

   public static void notify(String alarmType, String title, String content, boolean isNow) {
      Class<?> clazz = ReflectionUtils.classForName("com.kuma.boot.monitor.warn.WarnProvider");
      Object bean = ContextUtils.getBean(clazz, false);
      if (bean != null) {
         if (isNow) {
            ReflectionUtils.callMethodWithParams(bean, "notifynow", new String[]{alarmType, title, content}, new Class[]{String.class, String.class, String.class});
         } else {
            ReflectionUtils.callMethodWithParams(bean, "notify", new String[]{alarmType, title, content}, new Class[]{String.class, String.class, String.class});
         }
      }

   }
}
