package com.kuma.boot.eventbus.disruptor.tmp3.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;

public class AbstractAdviceEventHandler<T extends DisruptorEvent> extends AbstractEnabledEventHandler<T> {
   public AbstractAdviceEventHandler() {
   }

   protected boolean preHandle(T event) throws Exception {
      return true;
   }

   protected void postHandle(T event) throws Exception {
   }

   public void afterCompletion(T event, Exception exception) throws Exception {
   }

   protected void executeChain(T event, HandlerChain<T> chain) throws Exception {
      chain.doHandler(event);
   }

   public void doHandlerInternal(T event, HandlerChain<T> handlerChain) throws Exception {
      if (!this.isEnabled(event)) {
         LogUtils.debug("Handler '{}' is not enabled for the current event.  Proceeding without invoking this handler.", new Object[]{this.getName()});
         handlerChain.doHandler(event);
      } else {
         LogUtils.info("Handler '{}' enabled.  Executing now.", new Object[]{this.getName()});
         Exception exception = null;

         try {
            boolean continueChain = this.preHandle(event);
            LogUtils.info("Invoked preHandle method.  Continuing chain?: [" + continueChain + "]", new Object[0]);
            if (continueChain) {
               this.executeChain(event, handlerChain);
            }

            this.postHandle(event);
            LogUtils.info("Successfully invoked postHandle method", new Object[0]);
         } catch (Exception e) {
            exception = e;
         } finally {
            this.cleanup(event, exception);
         }
      }

   }

   protected void cleanup(T event, Exception existing) throws Exception {
      Exception exception = existing;

      try {
         this.afterCompletion(event, exception);
         LogUtils.info("Successfully invoked afterCompletion method.", new Object[0]);
      } catch (Exception e) {
         if (existing != null) {
            LogUtils.debug("afterCompletion implementation threw an exception.  This will be ignored to allow the original source exception to be propagated.", new Object[]{e});
         }
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
