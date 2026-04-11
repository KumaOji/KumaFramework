package com.kuma.boot.eventbus.atlas.core;

import java.util.concurrent.ExecutorService;

public class EventBusFactory {
   private static EventBus defaultEventBus;
   private static EventBus asyncEventBus;
   private static EventBus orderedEventBus;
   private static EventBus adaptiveThreadPoolEventBus;
   private static EventBus multiThreadPoolEventBus;

   public EventBusFactory() {
   }

   public static synchronized EventBus getDefaultEventBus() {
      if (defaultEventBus == null) {
         defaultEventBus = new DefaultEventBus();
      }

      return defaultEventBus;
   }

   public static synchronized EventBus getAsyncEventBus() {
      if (asyncEventBus == null) {
         int threadPoolSize = Runtime.getRuntime().availableProcessors();
         asyncEventBus = new AsyncEventBus(threadPoolSize);
      }

      return asyncEventBus;
   }

   public static synchronized EventBus getOrderedEventBus() {
      if (orderedEventBus == null) {
         orderedEventBus = new OrderedEventBus(getDefaultEventBus());
      }

      return orderedEventBus;
   }

   public static synchronized EventBus getAdaptiveThreadPoolEventBus() {
      if (adaptiveThreadPoolEventBus == null) {
         int corePoolSize = Runtime.getRuntime().availableProcessors();
         int maxPoolSize = corePoolSize * 2;
         adaptiveThreadPoolEventBus = new AdaptiveThreadPoolEventBus(getDefaultEventBus(), corePoolSize, maxPoolSize, 0.7, 30);
      }

      return adaptiveThreadPoolEventBus;
   }

   public static synchronized EventBus getMultiThreadPoolEventBus() {
      if (multiThreadPoolEventBus == null) {
         int defaultThreadPoolSize = Runtime.getRuntime().availableProcessors();
         multiThreadPoolEventBus = new MultiThreadPoolEventBus(getDefaultEventBus(), defaultThreadPoolSize);
      }

      return multiThreadPoolEventBus;
   }

   public static EventBus createSyncEventBus() {
      return new DefaultEventBus();
   }

   public static EventBus createAsyncEventBus(int threadPoolSize) {
      return new AsyncEventBus(threadPoolSize);
   }

   public static EventBus createAsyncEventBus(ExecutorService executorService) {
      return new AsyncEventBus(executorService);
   }

   public static EventBus createOrderedEventBus(EventBus delegate) {
      return new OrderedEventBus(delegate);
   }

   public static EventBus createAdaptiveThreadPoolEventBus(EventBus delegate, int corePoolSize, int maxPoolSize, double targetUtilization, int monitorIntervalSeconds) {
      return new AdaptiveThreadPoolEventBus(delegate, corePoolSize, maxPoolSize, targetUtilization, monitorIntervalSeconds);
   }

   public static EventBus createMultiThreadPoolEventBus(EventBus delegate, int defaultThreadPoolSize) {
      return new MultiThreadPoolEventBus(delegate, defaultThreadPoolSize);
   }

   public static EventBus createKafkaDistributedEventBus(EventBus localEventBus, String bootstrapServers, String topic, String groupId) {
      return new KafkaDistributedEventBus(localEventBus, bootstrapServers, topic, groupId);
   }

   public static synchronized void shutdownAll() {
      if (asyncEventBus instanceof AsyncEventBus) {
         ((AsyncEventBus)asyncEventBus).shutdown();
      }

      if (orderedEventBus instanceof OrderedEventBus) {
         ((OrderedEventBus)orderedEventBus).shutdown();
      }

      if (adaptiveThreadPoolEventBus instanceof AdaptiveThreadPoolEventBus) {
         ((AdaptiveThreadPoolEventBus)adaptiveThreadPoolEventBus).shutdown();
      }

      if (multiThreadPoolEventBus instanceof MultiThreadPoolEventBus) {
         ((MultiThreadPoolEventBus)multiThreadPoolEventBus).shutdown();
      }

      defaultEventBus = null;
      asyncEventBus = null;
      orderedEventBus = null;
      adaptiveThreadPoolEventBus = null;
      multiThreadPoolEventBus = null;
   }
}
