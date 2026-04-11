package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;

public abstract class AbstractEnabledEventHandler<T extends DisruptorEvent> extends AbstractNameableEventHandler<T> {
   protected boolean enabled = true;

   public AbstractEnabledEventHandler() {
   }

   protected abstract void doHandlerInternal(T event, HandlerChain<T> handlerChain) throws Exception;

   public void doHandler(T event, HandlerChain<T> handlerChain) throws Exception {
      if (!this.isEnabled(event)) {
         LogUtils.debug("Handler '{}' is not enabled for the current event.  Proceeding without invoking this handler.", new Object[]{this.getName()});
         handlerChain.doHandler(event);
      } else {
         LogUtils.info("Handler '{}' enabled.  Executing now.", new Object[]{this.getName()});
         this.doHandlerInternal(event, handlerChain);
      }

   }

   protected boolean isEnabled(T event) throws Exception {
      return this.isEnabled();
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
