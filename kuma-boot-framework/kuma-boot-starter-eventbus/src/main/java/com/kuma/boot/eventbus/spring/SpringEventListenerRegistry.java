package com.kuma.boot.eventbus.spring;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.EventListener;
import com.kuma.boot.eventbus.EventListenerRegistry;
import com.kuma.boot.eventbus.model.EventModel;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.context.ApplicationContext;

public class SpringEventListenerRegistry implements EventListenerRegistry<EventModel> {
   final ApplicationContext applicationContext;
   final List<EventListener> eventListeners;

   public SpringEventListenerRegistry(ApplicationContext applicationContext, List<EventListener> eventListeners) {
      this.applicationContext = applicationContext;
      this.eventListeners = eventListeners;
   }

   public void initRegistryEventListener(List<EventListener> eventConsumerList) {
   }

   public void publish(EventModel param) {
      this.applicationContext.publishEvent(param);
   }

   @PostConstruct
   public void init() {
      LogUtils.info("\u5f00\u59cb\u521d\u59cb\u5316Spring\u4e8b\u4ef6\u76d1\u542c\u5668\u7684\u7ec4\u4ef6\u670d\u52a1", new Object[0]);
      this.initRegistryEventListener(this.eventListeners);
      LogUtils.info("\u5b8c\u6210\u521d\u59cb\u5316Spring\u4e8b\u4ef6\u76d1\u542c\u5668\u7684\u7ec4\u4ef6\u670d\u52a1", new Object[0]);
   }
}
