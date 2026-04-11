package com.kuma.boot.eventbus.enums;

import cn.hutool.core.util.StrUtil;
import java.util.HashMap;
import java.util.Map;

public enum EventBusTypeEnum {
   spring,
   guava,
   greenrobot,
   disruptor;

   public static final Map<EventBusTypeEnum, String> typeClass = new HashMap();

   private EventBusTypeEnum() {
   }

   public static String getBeanName(EventBusTypeEnum eventBusTypeEnum) {
      String className = (String)typeClass.get(eventBusTypeEnum);
      if (StrUtil.isEmpty(className)) {
         throw new RuntimeException("\u672a\u627e\u5230\u5bf9\u5e94\u7684\u7c7b\u578b");
      } else {
         return className;
      }
   }

   // $FF: synthetic method
   private static EventBusTypeEnum[] $values() {
      return new EventBusTypeEnum[]{spring, guava, greenrobot, disruptor};
   }

   static {
      typeClass.put(spring, "springEventListenerRegistry");
      typeClass.put(guava, "guavaEventListenerRegistry");
      typeClass.put(greenrobot, "greenrobotEventListenerRegistry");
      typeClass.put(disruptor, "disruptorEventListenerRegistry");
   }
}
