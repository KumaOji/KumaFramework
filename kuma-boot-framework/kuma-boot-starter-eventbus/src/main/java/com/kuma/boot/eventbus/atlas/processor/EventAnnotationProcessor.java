package com.kuma.boot.eventbus.atlas.processor;

import com.kuma.boot.eventbus.atlas.annotation.EventPublish;
import com.kuma.boot.eventbus.atlas.annotation.EventSubscribe;
import com.kuma.boot.eventbus.atlas.core.EventBus;
import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 由 {@link com.kuma.boot.eventbus.atlas.config.EventAutoConfiguration} 注册。
 * <p>{@link BeanPostProcessor} 会在容器极早阶段实例化，若构造器直接依赖 {@link EventBus}，此时 {@code eventBus()} 可能尚未创建。
 * 使用 {@link ObjectProvider} 延迟解析，在首次回调时再 {@link ObjectProvider#getObject()}。</p>
 */
public class EventAnnotationProcessor implements BeanPostProcessor {
   private final ObjectProvider<EventBus> eventBusProvider;

   public EventAnnotationProcessor( ObjectProvider<EventBus> eventBusProvider ) {
      this.eventBusProvider = eventBusProvider;
   }

   public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
      if (this.hasEventSubscribeAnnotation(bean)) {
         this.eventBusProvider.getObject().register(bean);
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
