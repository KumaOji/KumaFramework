package com.kuma.boot.eventbus.disruptor.tmp5.service;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolExecutorService {
   public ThreadPoolExecutorService() {
   }

   public static void execute(Runnable runnable, ThreadPoolExecutor executor) {
      if (runnable != null && executor != null) {
         if (!executor.isShutdown()) {
            executor.execute(runnable);
         }
      }
   }

   public static Future submit1(Runnable runnable, ThreadPoolExecutor executor) {
      if (runnable != null && executor != null) {
         return executor.isShutdown() ? null : executor.submit(runnable);
      } else {
         return null;
      }
   }

   public static <T> Future<T> submit2(Callable<T> callable, ThreadPoolExecutor executor) {
      if (callable != null && executor != null) {
         return executor.isShutdown() ? null : executor.submit(callable);
      } else {
         return null;
      }
   }

   public static void cancel(Runnable runnable, ThreadPoolExecutor executor) {
      if (runnable != null && executor != null) {
         if (!executor.isShutdown()) {
            executor.getQueue().remove(runnable);
         }
      }
   }
}
