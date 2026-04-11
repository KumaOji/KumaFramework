package com.kuma.boot.eventbus.disruptor.tmp.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import org.jspecify.annotations.NonNull;

public class DisruptorThreadFactory implements ThreadFactory {
   private static final AtomicLong index = new AtomicLong(1L);

   public DisruptorThreadFactory() {
   }

   public Thread newThread(@NonNull Runnable r) {
      return new Thread(r, "disruptor-" + index.getAndIncrement());
   }
}
