package com.kuma.boot.eventbus.disruptor.tmp5.handler;

import com.lmax.disruptor.EventHandler;
import com.kuma.boot.eventbus.disruptor.tmp5.event.DisruptorEvent;
import org.springframework.stereotype.Component;

@Component
public class ClearingWorkHandler implements EventHandler<DisruptorEvent> {
   public ClearingWorkHandler() {
   }

   public void onEvent(DisruptorEvent event, long l, boolean b) throws Exception {
      event.clear();
   }
}
