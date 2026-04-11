package com.kuma.boot.eventbus.disruptor.tmp3.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DisruptorEventAwareProcessor implements ApplicationContextAware, BeanPostProcessor, InitializingBean {
   private DisruptorApplicationContext disruptorContext;
   private ApplicationContext applicationContext;

   public DisruptorEventAwareProcessor() {
   }

   public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
      this.invokeAwareInterfaces(bean);
      return bean;
   }

   protected void invokeAwareInterfaces(Object bean) {
      if (bean instanceof Aware && bean instanceof DisruptorEventPublisherAware) {
         DisruptorEventPublisherAware awareBean = (DisruptorEventPublisherAware)bean;
         awareBean.setDisruptorEventPublisher(this.disruptorContext);
      }

   }

   public Object postProcessAfterInitialization(Object bean, String beanName) {
      return bean;
   }

   public void afterPropertiesSet() throws Exception {
      this.disruptorContext = new DisruptorApplicationContext();
      this.disruptorContext.setApplicationContext(this.applicationContext);
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }
}
