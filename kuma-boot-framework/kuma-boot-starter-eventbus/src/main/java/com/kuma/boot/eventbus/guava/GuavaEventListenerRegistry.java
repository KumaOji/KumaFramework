package com.kuma.boot.eventbus.guava;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.EventListener;
import com.kuma.boot.eventbus.EventListenerRegistry;
import com.kuma.boot.eventbus.model.EventModel;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import org.springframework.objenesis.instantiator.util.ClassUtils;

public class GuavaEventListenerRegistry implements EventListenerRegistry<EventModel> {
   private EventBus eventBus;
   final List<EventListener> eventListeners;
   final Executor guavaEventBusExecutor;

   public GuavaEventListenerRegistry(List<EventListener> eventListeners, Executor guavaEventBusExecutor) {
      this.eventListeners = eventListeners;
      this.guavaEventBusExecutor = guavaEventBusExecutor;
   }

   public void initRegistryEventListener(List<EventListener> eventConsumerList) {
      this.eventBus = new AsyncEventBus(this.guavaEventBusExecutor, new SubscriberExceptionHandler() {
         {
            Objects.requireNonNull(GuavaEventListenerRegistry.this);
         }

         public void handleException(Throwable e, SubscriberExceptionContext context) {
            LogUtils.error("asfdasdf", new Object[]{e});
         }
      });
      eventConsumerList.forEach((param) -> {
         LogUtils.info("\u6ce8\u518c\u76d1\u542c\u5668\uff1a{}", new Object[]{param.getClass().getName()});
         this.eventBus.register(ClassUtils.newInstance(param.getClass()));
      });
   }

   public void publish(EventModel param) {
      this.eventBus.post(param);
   }

   @PostConstruct
   public void init() {
      LogUtils.info("\u5f00\u59cb\u521d\u59cb\u5316Guava\u4e8b\u4ef6\u76d1\u542c\u5668\u7684\u7ec4\u4ef6\u670d\u52a1", new Object[0]);
      this.initRegistryEventListener(this.eventListeners);
      LogUtils.info("\u5b8c\u6210\u521d\u59cb\u5316Guava\u4e8b\u4ef6\u76d1\u542c\u5668\u7684\u7ec4\u4ef6\u670d\u52a1", new Object[0]);
   }
}
