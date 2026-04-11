package com.kuma.boot.eventbus.disruptor.tmp3.hooks;

import com.lmax.disruptor.dsl.Disruptor;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;

public class DisruptorShutdownHook extends Thread {
   private Disruptor<DisruptorEvent> disruptor;

   public DisruptorShutdownHook(Disruptor<DisruptorEvent> disruptor) {
      this.disruptor = disruptor;
   }

   public void run() {
      this.disruptor.shutdown();
   }
}
