package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.lmax.disruptor.EventHandler;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;
import org.springframework.core.Ordered;

public class DisruptorEventDispatcher extends AbstractRouteableEventHandler<DisruptorEvent> implements EventHandler<DisruptorEvent>, Ordered {
   private int order;

   public DisruptorEventDispatcher(HandlerChainResolver<DisruptorEvent> filterChainResolver, int order) {
      super(filterChainResolver);
      this.order = order;
   }

   public void onEvent(DisruptorEvent event, long sequence, boolean endOfBatch) throws Exception {
      HandlerChain<DisruptorEvent> originalChain = new ProxiedHandlerChain();
      this.doHandler(event, originalChain);
   }

   public int getOrder() {
      return this.order;
   }
}
