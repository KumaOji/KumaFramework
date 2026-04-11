package com.kuma.boot.eventbus.disruptor.tmp5.utils;

import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.InvocationTargetException;

public class LoaderUtil {
   public LoaderUtil() {
   }

   public static ClassLoader getClassLoader() {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      if (cl != null) {
         return cl;
      } else {
         ClassLoader ccl = LoaderUtil.class.getClassLoader();
         return ccl == null ? ClassLoader.getSystemClassLoader() : ccl;
      }
   }

   public static Class<?> initializeClass(final String className, final ClassLoader loader) throws ClassNotFoundException {
      return Class.forName(className, true, loader);
   }

   public static Class<?> loadClass(final String className, final ClassLoader loader) throws ClassNotFoundException {
      return loader != null ? loader.loadClass(className) : null;
   }

   public static Class<?> loadSystemClass(final String className) throws ClassNotFoundException {
      try {
         return Class.forName(className, true, ClassLoader.getSystemClassLoader());
      } catch (Throwable t) {
         LogUtils.error("LoaderUtil Couldn't use SystemClassLoader. Trying Class.forName({}).", new Object[]{className, t});
         return Class.forName(className);
      }
   }

   public static <T> T newInstanceOf(Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
      try {
         return (T)clazz.getConstructor().newInstance();
      } catch (NoSuchMethodException var2) {
         return (T)clazz.newInstance();
      }
   }
}
