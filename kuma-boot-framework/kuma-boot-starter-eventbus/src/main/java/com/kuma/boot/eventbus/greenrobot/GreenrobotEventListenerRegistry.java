package com.kuma.boot.eventbus.greenrobot;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.EventListener;
import com.kuma.boot.eventbus.EventListenerRegistry;
import com.kuma.boot.eventbus.model.EventModel;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.objenesis.instantiator.util.ClassUtils;

public class GreenrobotEventListenerRegistry implements EventListenerRegistry<EventModel> {
   private EventBus eventBus;
   private final ApplicationContext applicationContext;
   private final ExecutorService executorService;
   final List<EventListener> eventListeners;

   public GreenrobotEventListenerRegistry(ApplicationContext applicationContext, ExecutorService executorService, List<EventListener> eventListeners) {
      this.applicationContext = applicationContext;
      this.executorService = executorService;
      this.eventListeners = eventListeners;
   }

   public void initRegistryEventListener(List<EventListener> eventConsumerList) {
      this.eventBus = EventBus.builder().eventInheritance(false).throwSubscriberException(true).executorService(this.executorService).logger(new LoggerImpl()).build();
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
      LogUtils.info("\u5f00\u59cb\u521d\u59cb\u5316Spring\u4e8b\u4ef6\u76d1\u542c\u5668\u7684\u7ec4\u4ef6\u670d\u52a1", new Object[0]);
      this.initRegistryEventListener(this.eventListeners);
      LogUtils.info("\u5b8c\u6210\u521d\u59cb\u5316Spring\u4e8b\u4ef6\u76d1\u542c\u5668\u7684\u7ec4\u4ef6\u670d\u52a1", new Object[0]);
   }

   static class LoggerImpl implements Logger {
      LoggerImpl() {
      }

      public void log(Level level, String s) {
         LogUtils.info(s, new Object[0]);
      }

      public void log(Level level, String s, Throwable throwable) {
         LogUtils.info(s, new Object[]{throwable});
      }
   }
}
