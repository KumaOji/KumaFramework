package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;
import com.kuma.boot.eventbus.disruptor.tmp3.exception.EventHandleException;
import java.io.IOException;

public class AbstractRouteableEventHandler<T extends DisruptorEvent> extends AbstractEnabledEventHandler<T> {
   protected HandlerChainResolver<T> handlerChainResolver;

   public AbstractRouteableEventHandler() {
   }

   public AbstractRouteableEventHandler(HandlerChainResolver<T> handlerChainResolver) {
      this.handlerChainResolver = handlerChainResolver;
   }

   protected void doHandlerInternal(T event, HandlerChain<T> handlerChain) throws Exception {
      Throwable t = null;

      try {
         this.executeChain(event, handlerChain);
      } catch (Throwable throwable) {
         t = throwable;
      }

      if (t != null) {
         if (t instanceof IOException) {
            throw (IOException)t;
         } else {
            String msg = "Handlered event failed.";
            throw new EventHandleException(msg, t);
         }
      }
   }

   protected HandlerChain<T> getExecutionChain(T event, HandlerChain<T> origChain) {
      HandlerChain<T> chain = origChain;
      HandlerChainResolver<T> resolver = this.getHandlerChainResolver();
      if (resolver == null) {
         LogUtils.debug("No HandlerChainResolver configured.  Returning original HandlerChain.", new Object[0]);
         return origChain;
      } else {
         HandlerChain<T> resolved = resolver.getChain(event, origChain);
         if (resolved != null) {
            LogUtils.info("Resolved a configured HandlerChain for the current event.", new Object[0]);
            chain = resolved;
         } else {
            LogUtils.info("No HandlerChain configured for the current event.  Using the default.", new Object[0]);
         }

         return chain;
      }
   }

   protected void executeChain(T event, HandlerChain<T> origChain) throws Exception {
      HandlerChain<T> chain = this.getExecutionChain(event, origChain);
      chain.doHandler(event);
   }

   public HandlerChainResolver<T> getHandlerChainResolver() {
      return this.handlerChainResolver;
   }

   public void setHandlerChainResolver(HandlerChainResolver<T> handlerChainResolver) {
      this.handlerChainResolver = handlerChainResolver;
   }
}
