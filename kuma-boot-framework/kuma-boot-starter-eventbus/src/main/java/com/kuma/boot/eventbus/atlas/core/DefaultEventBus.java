package com.kuma.boot.eventbus.atlas.core;

import com.kuma.boot.eventbus.atlas.annotation.EventSubscribe;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.util.ReflectionUtils;

public class DefaultEventBus implements EventBus {
   private final Map<String, List<EventListener>> listeners = new ConcurrentHashMap();

   public DefaultEventBus() {
   }

   public void publish(Event event) {
      String eventType = event.getType();
      List<EventListener> eventListeners = (List)this.listeners.get(eventType);
      if (eventListeners != null) {
         for(EventListener listener : eventListeners) {
            if (listener.isAsync()) {
               (new Thread(() -> this.invokeListener(listener, event))).start();
            } else {
               this.invokeListener(listener, event);
            }
         }
      }

   }

   public void register(Object listener) {
      this.scanAndRegister(listener);
   }

   public void unregister(Object listener) {
   }

   public void scanAndRegister(Object listener) {
      Class<?> listenerClass = listener.getClass();
      Method[] methods = ReflectionUtils.getAllDeclaredMethods(listenerClass);

      for(Method method : methods) {
         if (method.isAnnotationPresent(EventSubscribe.class)) {
            EventSubscribe annotation = (EventSubscribe)method.getAnnotation(EventSubscribe.class);
            String eventType = annotation.eventType();
            if (eventType.isEmpty() && method.getParameterTypes().length > 0) {
               eventType = method.getParameterTypes()[0].getName();
            }

            if (!eventType.isEmpty()) {
               EventListener eventListener = new EventListener(listener, method, annotation.async(), annotation.threadPool());
               ((List)this.listeners.computeIfAbsent(eventType, (k) -> new CopyOnWriteArrayList())).add(eventListener);
            }
         }
      }

   }

   private void invokeListener(EventListener listener, Event event) {
      try {
         listener.getMethod().invoke(listener.getTarget(), event);
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   private static class EventListener {
      private final Object target;
      private final Method method;
      private final boolean async;
      private final String threadPool;

      public EventListener(Object target, Method method, boolean async, String threadPool) {
         this.target = target;
         this.method = method;
         this.async = async;
         this.threadPool = threadPool;
      }

      public Object getTarget() {
         return this.target;
      }

      public Method getMethod() {
         return this.method;
      }

      public boolean isAsync() {
         return this.async;
      }

      public String getThreadPool() {
         return this.threadPool;
      }
   }
}
