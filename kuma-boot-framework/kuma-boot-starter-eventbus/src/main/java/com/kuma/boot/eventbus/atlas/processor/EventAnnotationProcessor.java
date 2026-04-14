package com.kuma.boot.eventbus.atlas.processor;

import com.kuma.boot.eventbus.atlas.annotation.EventPublish;
import com.kuma.boot.eventbus.atlas.annotation.EventSubscribe;
import com.kuma.boot.eventbus.atlas.core.EventBus;
import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/** 由 {@link com.kuma.boot.eventbus.atlas.config.EventAutoConfiguration} 导入，勿使用 {@code @Component} 单独扫描。 */
public class EventAnnotationProcessor implements BeanPostProcessor {
   private final EventBus eventBus;

   public EventAnnotationProcessor(EventBus eventBus) {
      this.eventBus = eventBus;
   }

   public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
      if (this.hasEventSubscribeAnnotation(bean)) {
         this.eventBus.register(bean);
      }

      if (bean.getClass().isAnnotationPresent(EventPublish.class)) {
         EventPublish eventPublish = (EventPublish)bean.getClass().getAnnotation(EventPublish.class);
         if (eventPublish.enable()) {
         }
      }

      return bean;
   }

   public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
      return bean;
   }

   private boolean hasEventSubscribeAnnotation(Object bean) {
      for(Class<?> clazz = bean.getClass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
         for(Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventSubscribe.class)) {
               return true;
            }
         }
      }

      return false;
   }
}
