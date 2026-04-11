package com.kuma.boot.eventbus.atlas.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadPoolEventBus implements EventBus {
   private final EventBus delegate;
   private final Map<String, ExecutorService> eventTypeToThreadPool = new ConcurrentHashMap();
   private final ExecutorService defaultThreadPool;
   private final Set<String> highPriorityEventTypes = ConcurrentHashMap.newKeySet();
   private final Set<String> lowPriorityEventTypes = ConcurrentHashMap.newKeySet();

   public MultiThreadPoolEventBus(EventBus delegate, int defaultThreadPoolSize) {
      this.delegate = delegate;
      this.defaultThreadPool = Executors.newFixedThreadPool(defaultThreadPoolSize, (r) -> {
         Thread t = new Thread(r, "default-event-processor");
         t.setDaemon(true);
         return t;
      });
   }

   public MultiThreadPoolEventBus configureThreadPool(String eventType, int threadPoolSize) {
      ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize, (r) -> {
         Thread t = new Thread(r, eventType + "-processor");
         t.setDaemon(true);
         return t;
      });
      this.eventTypeToThreadPool.put(eventType, threadPool);
      return this;
   }

   public MultiThreadPoolEventBus markAsHighPriority(String eventType) {
      this.highPriorityEventTypes.add(eventType);
      this.lowPriorityEventTypes.remove(eventType);
      return this;
   }

   public MultiThreadPoolEventBus markAsLowPriority(String eventType) {
      this.lowPriorityEventTypes.add(eventType);
      this.highPriorityEventTypes.remove(eventType);
      return this;
   }

   public void publish(Event event) {
      String eventType = event.getType();
      ExecutorService threadPool = (ExecutorService)this.eventTypeToThreadPool.getOrDefault(eventType, this.defaultThreadPool);
      if (this.highPriorityEventTypes.contains(eventType)) {
         threadPool.submit(() -> {
            Thread.currentThread().setPriority(10);
            this.delegate.publish(event);
         });
      } else if (this.lowPriorityEventTypes.contains(eventType)) {
         threadPool.submit(() -> {
            Thread.currentThread().setPriority(1);
            this.delegate.publish(event);
         });
      } else {
         threadPool.submit(() -> this.delegate.publish(event));
      }

   }

   public void register(Object listener) {
      this.delegate.register(listener);
   }

   public void unregister(Object listener) {
      this.delegate.unregister(listener);
   }

   public void scanAndRegister(Object listener) {
      this.delegate.scanAndRegister(listener);
   }

   public void shutdown() {
      this.defaultThreadPool.shutdown();

      for(ExecutorService threadPool : this.eventTypeToThreadPool.values()) {
         threadPool.shutdown();
      }

   }
}
