package com.kuma.boot.eventbus.disruptor.tmp3.context;

import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;
import java.lang.reflect.Constructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;

public class EventPublicationInterceptor implements MethodInterceptor, DisruptorEventPublisherAware, InitializingBean {
   private Constructor<?> applicationEventClassConstructor;
   private DisruptorEventPublisher applicationEventPublisher;

   public EventPublicationInterceptor() {
   }

   public void setApplicationEventClass(Class<?> applicationEventClass) {
      if (DisruptorEvent.class != applicationEventClass && DisruptorEvent.class.isAssignableFrom(applicationEventClass)) {
         try {
            this.applicationEventClassConstructor = applicationEventClass.getConstructor(Object.class);
         } catch (NoSuchMethodException ex) {
            String var10002 = applicationEventClass.getName();
            throw new IllegalArgumentException("applicationEventClass [" + var10002 + "] does not have the required Object constructor: " + String.valueOf(ex));
         }
      } else {
         throw new IllegalArgumentException("applicationEventClass needs to extend DisruptorEvent");
      }
   }

   public void setDisruptorEventPublisher(DisruptorEventPublisher applicationEventPublisher) {
      this.applicationEventPublisher = applicationEventPublisher;
   }

   public void afterPropertiesSet() throws Exception {
      if (this.applicationEventClassConstructor == null) {
         throw new IllegalArgumentException("applicationEventClass is required");
      }
   }

   public Object invoke(MethodInvocation invocation) throws Throwable {
      Object retVal = invocation.proceed();
      DisruptorEvent event = (DisruptorEvent)this.applicationEventClassConstructor.newInstance(invocation.getThis());
      this.applicationEventPublisher.publishEvent(event);
      return retVal;
   }
}
