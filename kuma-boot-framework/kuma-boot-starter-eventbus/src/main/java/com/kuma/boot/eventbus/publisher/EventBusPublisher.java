package com.kuma.boot.eventbus.publisher;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.EventListenerRegistry;
import com.kuma.boot.eventbus.enums.EventBusTypeEnum;
import com.kuma.boot.eventbus.model.EventModel;
import java.util.Map;

public class EventBusPublisher {
   private final Map<String, EventListenerRegistry> eventListenerRegistryMap;

   public EventBusPublisher(Map<String, EventListenerRegistry> eventListenerRegistryMap) {
      this.eventListenerRegistryMap = eventListenerRegistryMap;
   }

   public void publish(EventModel eventModel, EventBusTypeEnum eventBusTypeEnum) {
      String beanName = EventBusTypeEnum.getBeanName(eventBusTypeEnum);
      EventListenerRegistry eventListenerRegistry = (EventListenerRegistry)this.eventListenerRegistryMap.get(beanName);
      eventListenerRegistry.publish(eventModel);
      LogUtils.info("event was post,id:{},name:{}", new Object[]{eventModel.getTopic(), eventModel.getClass().getName()});
   }
}
