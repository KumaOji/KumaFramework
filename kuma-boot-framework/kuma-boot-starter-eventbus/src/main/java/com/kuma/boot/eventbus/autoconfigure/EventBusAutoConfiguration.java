package com.kuma.boot.eventbus.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.EventListener;
import com.kuma.boot.eventbus.autoconfigure.properties.EventBusProperties;
import com.kuma.boot.eventbus.disruptor.DisruptorEventListenerRegistry;
import com.kuma.boot.eventbus.greenrobot.GreenrobotEventBusExecutor;
import com.kuma.boot.eventbus.greenrobot.GreenrobotEventBusExecutorProperties;
import com.kuma.boot.eventbus.greenrobot.GreenrobotEventListenerRegistry;
import com.kuma.boot.eventbus.guava.GuavaEventBusExecutor;
import com.kuma.boot.eventbus.guava.GuavaEventBusExecutorProperties;
import com.kuma.boot.eventbus.guava.GuavaEventListenerRegistry;
import com.kuma.boot.eventbus.spring.SpringEventListenerRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@AutoConfiguration
@EnableConfigurationProperties({EventBusProperties.class, GuavaEventBusExecutorProperties.class, GreenrobotEventBusExecutorProperties.class})
public class EventBusAutoConfiguration implements ApplicationContextAware, InitializingBean {
   private ApplicationContext applicationContext;
   private final ObjectProvider<List<EventListener>> eventListenersObjectProvider;

   public EventBusAutoConfiguration(ObjectProvider<List<EventListener>> eventListenersObjectProvider) {
      this.eventListenersObjectProvider = eventListenersObjectProvider;
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(EventBusAutoConfiguration.class, "kuma-boot-starter-eventbus", new String[0]);
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }

   private List<EventListener> eventListeners() {
      return (List)this.eventListenersObjectProvider.getIfAvailable(ArrayList::new);
   }

   @Bean
   public SpringEventListenerRegistry springEventListenerRegistry() {
      return new SpringEventListenerRegistry(this.applicationContext, this.eventListeners());
   }

   @Bean({"guavaEventBusExecutor"})
   public Executor guavaEventBusExecutor(GuavaEventBusExecutorProperties executorProperites) {
      GuavaEventBusExecutor guavaEventBusExecutor = new GuavaEventBusExecutor(executorProperites);
      return guavaEventBusExecutor.getThreadPoolTaskExecutor();
   }

   @Bean
   public GuavaEventListenerRegistry guavaEventListenerRegistry(@Autowired @Qualifier("guavaEventBusExecutor") Executor guavaEventBusExecutor) {
      return new GuavaEventListenerRegistry(this.eventListeners(), guavaEventBusExecutor);
   }

   @Bean({"greenrobotEventBusExecutor"})
   public ExecutorService greenrobotEventBusExecutor(GreenrobotEventBusExecutorProperties executorProperites) {
      GreenrobotEventBusExecutor guavaEventBusExecutor = new GreenrobotEventBusExecutor(executorProperites);
      return guavaEventBusExecutor.getThreadPoolTaskExecutor();
   }

   @Bean
   public GreenrobotEventListenerRegistry greenrobotEventListenerRegistry(@Autowired @Qualifier("greenrobotEventBusExecutor") ExecutorService greenrobotEventBusExecutor) {
      return new GreenrobotEventListenerRegistry(this.applicationContext, greenrobotEventBusExecutor, this.eventListeners());
   }

   @Bean
   @Scope("prototype")
   public DisruptorEventListenerRegistry disruptorEventListenerRegistry() {
      return new DisruptorEventListenerRegistry(this.eventListeners());
   }
}
