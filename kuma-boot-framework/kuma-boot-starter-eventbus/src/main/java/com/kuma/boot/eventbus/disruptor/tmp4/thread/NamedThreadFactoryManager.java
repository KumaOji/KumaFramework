package com.kuma.boot.eventbus.disruptor.tmp4.thread;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

public enum NamedThreadFactoryManager {
   INSTANCE;

   private final ConcurrentHashMap<String, ThreadFactory> namedThreadFactories = new ConcurrentHashMap();

   private NamedThreadFactoryManager() {
   }

   public ThreadFactory acquire(String name) {
      return (ThreadFactory)this.namedThreadFactories.computeIfAbsent(name, NamedDaemonThreadFactory::new);
   }

   // $FF: synthetic method
   private static NamedThreadFactoryManager[] $values() {
      return new NamedThreadFactoryManager[]{INSTANCE};
   }
}
