package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;
import java.util.List;

public class ProxiedHandlerChain implements HandlerChain<DisruptorEvent> {
   private ProxiedHandlerChain originalChain;
   private List<DisruptorHandler<DisruptorEvent>> handlers;
   private int currentPosition = 0;

   public ProxiedHandlerChain() {
      this.currentPosition = -1;
   }

   public ProxiedHandlerChain(ProxiedHandlerChain orig, List<DisruptorHandler<DisruptorEvent>> handlers) {
      if (orig == null) {
         throw new NullPointerException("original HandlerChain cannot be null.");
      } else {
         this.originalChain = orig;
         this.handlers = handlers;
         this.currentPosition = 0;
      }
   }

   public void doHandler(DisruptorEvent event) throws Exception {
      if (this.handlers != null && this.handlers.size() != this.currentPosition) {
         LogUtils.info("Invoking wrapped filter at index [" + this.currentPosition + "]", new Object[0]);
         ((DisruptorHandler)this.handlers.get(this.currentPosition++)).doHandler(event, this);
      } else {
         LogUtils.info("Invoking original filter chain.", new Object[0]);
         if (this.originalChain != null) {
            this.originalChain.doHandler(event);
         }
      }

   }
}
