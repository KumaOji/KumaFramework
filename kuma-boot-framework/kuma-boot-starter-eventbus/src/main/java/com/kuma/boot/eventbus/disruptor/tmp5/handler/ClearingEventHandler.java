package com.kuma.boot.eventbus.disruptor.tmp5.handler;

import com.lmax.disruptor.EventHandler;
import com.kuma.boot.eventbus.disruptor.tmp5.event.DisruptorEvent;
import org.springframework.stereotype.Component;

@Component
public class ClearingEventHandler implements EventHandler<DisruptorEvent> {
   public ClearingEventHandler() {
   }

   public void onEvent(DisruptorEvent event, long sequence, boolean endOfBatch) throws Exception {
      event.clear();
   }
}
