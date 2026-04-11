package com.kuma.boot.eventbus.disruptor.tmp.factory;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import com.kuma.boot.eventbus.disruptor.tmp.support.DisruptorCustomizer;
import com.kuma.boot.eventbus.disruptor.tmp.support.SpringDisruptor;
import com.kuma.boot.eventbus.disruptor.tmp.support.SpringEventHandler;
import java.util.concurrent.ThreadFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.StringUtils;

public class DisruptorFactoryBean<T> implements FactoryBean<SpringDisruptor<T>>, BeanFactoryAware, InitializingBean, DisposableBean {
   private AnnotationAttributes attributes;
   private BeanFactory beanFactory;
   private SpringDisruptor<T> disruptor;
   private Class<T> type;

   public DisruptorFactoryBean() {
   }

   public void setAttributes(AnnotationAttributes attributes) {
      this.attributes = attributes;
   }

   public void setType(Class<T> type) {
      this.type = type;
   }

   public SpringDisruptor<T> getObject() {
      return this.disruptor;
   }

   public Class<?> getObjectType() {
      return SpringDisruptor.class;
   }

   public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
      this.beanFactory = beanFactory;
   }

   private ThreadFactory createThreadFactory() {
      String threadFactoryBeanName = this.attributes.getString("threadFactoryBeanName");
      if (StringUtils.hasText(threadFactoryBeanName)) {
         return (ThreadFactory)this.beanFactory.getBean(threadFactoryBeanName, ThreadFactory.class);
      } else {
         Class<? extends ThreadFactory> factoryClass = this.attributes.getClass("threadFactory");
         return (ThreadFactory)BeanUtils.instantiateClass(factoryClass);
      }
   }

   private WaitStrategy createWaitStrategy() {
      String strategyBeanName = this.attributes.getString("strategyBeanName");
      if (StringUtils.hasText(strategyBeanName)) {
         return (WaitStrategy)this.beanFactory.getBean(strategyBeanName, WaitStrategy.class);
      } else {
         Class<? extends WaitStrategy> strategyClass = this.attributes.getClass("strategy");
         return (WaitStrategy)BeanUtils.instantiateClass(strategyClass);
      }
   }

   public void afterPropertiesSet() {
      SpringEventFactory<T> factory = new SpringEventFactory<T>();
      ResolvableType disruptorCustomizerType = ResolvableType.forClassWithGenerics(DisruptorCustomizer.class, new Class[]{this.type});
      SpringEventHandler<T> springEventHandler = new SpringEventHandler<T>(this.beanFactory, this.type);
      int bufferSize = this.attributes.getNumber("bufferSize").intValue();
      ProducerType producerType = (ProducerType)this.attributes.getEnum("type");
      this.disruptor = new SpringDisruptor<T>(factory, bufferSize, this.createThreadFactory(), producerType, this.createWaitStrategy());
      this.disruptor.handleEventsWith(new EventHandler[]{springEventHandler});
      this.beanFactory.getBeanProvider(disruptorCustomizerType).forEach((customizer) -> customizer.customize(this.disruptor));
      this.disruptor.start();
   }

   public void destroy() {
      this.disruptor.shutdown();
   }
}
