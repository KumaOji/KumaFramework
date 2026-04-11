package com.kuma.boot.eventbus.atlas.core;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderedEventBus implements EventBus {
   private final EventBus delegate;
   private final Map<String, BlockingQueue<Event>> orderKeyToQueue = new ConcurrentHashMap();
   private final Map<String, Thread> orderKeyToThread = new ConcurrentHashMap();
   private final ThreadFactory threadFactory = new OrderedEventThreadFactory();
   private volatile boolean running = true;

   public OrderedEventBus(EventBus delegate) {
      this.delegate = delegate;
   }

   public void publish(Event event) {
      if (event instanceof OrderedEvent) {
         String orderKey = ((OrderedEvent)event).getOrderKey();
         this.publishOrdered(event, orderKey);
      } else {
         this.delegate.publish(event);
      }

   }

   private void publishOrdered(Event event, String orderKey) {
      BlockingQueue<Event> queue = (BlockingQueue)this.orderKeyToQueue.computeIfAbsent(orderKey, (k) -> {
         BlockingQueue<Event> newQueue = new LinkedBlockingQueue();
         Thread processor = this.threadFactory.newThread(() -> this.processQueue(newQueue, k));
         processor.start();
         this.orderKeyToThread.put(k, processor);
         return newQueue;
      });

      try {
         queue.put(event);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new RuntimeException("Interrupted while queueing ordered event", e);
      }
   }

   private void processQueue(BlockingQueue<Event> queue, String orderKey) {
      try {
         while(this.running && !Thread.currentThread().isInterrupted()) {
            Event event = (Event)queue.take();

            try {
               this.delegate.publish(event);
            } catch (Exception e) {
               System.err.println("Error processing ordered event: " + event.getId());
               e.printStackTrace();
            }
         }
      } catch (InterruptedException var10) {
         Thread.currentThread().interrupt();
      } finally {
         this.orderKeyToQueue.remove(orderKey);
         this.orderKeyToThread.remove(orderKey);
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
      this.running = false;

      for(Thread thread : this.orderKeyToThread.values()) {
         thread.interrupt();
      }

      this.orderKeyToQueue.clear();
      this.orderKeyToThread.clear();
   }

   private static class OrderedEventThreadFactory implements ThreadFactory {
      private static final AtomicInteger poolNumber = new AtomicInteger(1);
      private final ThreadGroup group;
      private final AtomicInteger threadNumber = new AtomicInteger(1);
      private final String namePrefix;

      OrderedEventThreadFactory() {
         SecurityManager s = System.getSecurityManager();
         this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
         this.namePrefix = "ordered-event-processor-" + poolNumber.getAndIncrement() + "-";
      }

      public Thread newThread(Runnable r) {
         Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
         if (t.isDaemon()) {
            t.setDaemon(false);
         }

         if (t.getPriority() != 5) {
            t.setPriority(5);
         }

         return t;
      }
   }
}
