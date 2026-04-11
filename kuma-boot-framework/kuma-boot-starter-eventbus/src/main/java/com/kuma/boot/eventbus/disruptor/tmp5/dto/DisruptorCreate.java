package com.kuma.boot.eventbus.disruptor.tmp5.dto;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import com.kuma.boot.eventbus.disruptor.tmp5.builder.CustomThreadBuilder;
import com.kuma.boot.eventbus.disruptor.tmp5.builder.WaitStrategyBuilder;
import com.kuma.boot.eventbus.disruptor.tmp5.factory.CustomThreadFactory;
import com.kuma.boot.eventbus.disruptor.tmp5.factory.DisruptorEventFactory;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;

public class DisruptorCreate {
   private String threadFactoryName;
   private ProducerType producerType;
   private int bufferSize;
   private WaitStrategy waitStrategy;
   private EventFactory eventFactory;
   private ThreadFactory threadFactory;

   public DisruptorCreate() {
      String var10001 = UUID.randomUUID().toString();
      this.threadFactoryName = "disruptor-factory:" + var10001.replace("-", "");
      this.producerType = ProducerType.SINGLE;
      this.bufferSize = 1024;
      this.waitStrategy = WaitStrategyBuilder.builder().build().createWaitStrategy();
      this.eventFactory = new DisruptorEventFactory();
      this.threadFactory = new CustomThreadFactory(CustomThreadBuilder.builder().name(this.threadFactoryName).isDaemon(Boolean.TRUE).build());
   }

   public String getThreadFactoryName() {
      return this.threadFactoryName;
   }

   public void setThreadFactoryName(String threadFactoryName) {
      this.threadFactoryName = threadFactoryName;
   }

   public ProducerType getProducerType() {
      return this.producerType;
   }

   public void setProducerType(ProducerType producerType) {
      this.producerType = producerType;
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public void setBufferSize(int bufferSize) {
      this.bufferSize = bufferSize;
   }

   public WaitStrategy getWaitStrategy() {
      return this.waitStrategy;
   }

   public void setWaitStrategy(WaitStrategy waitStrategy) {
      this.waitStrategy = waitStrategy;
   }

   public EventFactory getEventFactory() {
      return this.eventFactory;
   }

   public void setEventFactory(EventFactory eventFactory) {
      this.eventFactory = eventFactory;
   }

   public ThreadFactory getThreadFactory() {
      return this.threadFactory;
   }

   public void setThreadFactory(ThreadFactory threadFactory) {
      this.threadFactory = threadFactory;
   }
}
