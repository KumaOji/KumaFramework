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
      LogUtils.info("开始初始化Spring事件监听器的组件服务", new Object[0]);
      this.initRegistryEventListener(this.eventListeners);
      LogUtils.info("完成初始化Spring事件监听器的组件服务", new Object[0]);
   }
}
