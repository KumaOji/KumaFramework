package com.kuma.boot.eventbus.disruptor.tmp4.event;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

public class AbstractEvent {
   private final AtomicBoolean available = new AtomicBoolean(true);
   private final LongAdder retireTimes = new LongAdder();

   public AbstractEvent() {
   }

   public boolean isAvailable() {
      return this.available.get();
   }

   public boolean doing() {
      return this.available.compareAndSet(true, false);
   }

   public boolean finished() {
      return this.available.compareAndSet(false, true);
   }

   public long getRetireTimes() {
      return this.retireTimes.longValue();
   }
}
