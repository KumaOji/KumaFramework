package com.kuma.boot.eventbus.disruptor.tmp3.configuration;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;
import com.kuma.boot.eventbus.disruptor.tmp3.handler.DisruptorEventDispatcher;
import com.kuma.boot.eventbus.disruptor.tmp3.properties.DisruptorProperties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass({Disruptor.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.eventbus.disruptor",
   value = {"enabled"},
   havingValue = "true"
)
public class RingBufferAutoConfiguration implements ApplicationContextAware, InitializingBean {
   private ApplicationContext applicationContext;

   public RingBufferAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(RingBufferAutoConfiguration.class, "kuma-boot-starter-eventbus", new String[0]);
   }

   @Bean
   @ConditionalOnClass({RingBuffer.class})
   @ConditionalOnProperty(
      prefix = "kuma.boot.eventbus.disruptor",
      value = {"ring-buffer"},
      havingValue = "true"
   )
   public RingBuffer<DisruptorEvent> ringBuffer(DisruptorProperties properties, WaitStrategy waitStrategy, EventFactory<DisruptorEvent> eventFactory, @Autowired(required = false) DisruptorEventDispatcher preEventHandler, @Autowired(required = false) DisruptorEventDispatcher postEventHandler) {
      ExecutorService executor = Executors.newFixedThreadPool(properties.getRingThreadNumbers());
      RingBuffer<DisruptorEvent> ringBuffer;
      if (properties.getMultiProducer()) {
         ringBuffer = RingBuffer.createMultiProducer(eventFactory, properties.getRingBufferSize(), waitStrategy);
      } else {
         ringBuffer = RingBuffer.createSingleProducer(eventFactory, properties.getRingBufferSize(), waitStrategy);
      }

      if (null != preEventHandler) {
         SequenceBarrier var8 = ringBuffer.newBarrier(new Sequence[0]);
      }

      return ringBuffer;
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }

   public ApplicationContext getApplicationContext() {
      return this.applicationContext;
   }
}
