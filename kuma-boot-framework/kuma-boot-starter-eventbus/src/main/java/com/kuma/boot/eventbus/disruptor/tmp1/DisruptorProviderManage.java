package com.kuma.boot.eventbus.disruptor.tmp1;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.kuma.boot.eventbus.disruptor.tmp1.consumer.QueueConsumer;
import com.kuma.boot.eventbus.disruptor.tmp1.consumer.QueueConsumerFactory;
import com.kuma.boot.eventbus.disruptor.tmp1.event.DataEvent;
import com.kuma.boot.eventbus.disruptor.tmp1.event.DisruptorEventFactory;
import com.kuma.boot.eventbus.disruptor.tmp1.event.OrderlyDisruptorEventFactory;
import com.kuma.boot.eventbus.disruptor.tmp1.provider.DisruptorProvider;
import com.kuma.boot.eventbus.disruptor.tmp1.thread.DisruptorThreadFactory;
import com.kuma.boot.eventbus.disruptor.tmp1.thread.OrderlyExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DisruptorProviderManage<T> {
   public static final Integer DEFAULT_SIZE = 16384;
   private static final Integer DEFAULT_CONSUMER_SIZE = Runtime.getRuntime().availableProcessors() << 1;
   private final Integer size;
   private final Integer consumerSize;
   private final QueueConsumerFactory<T> consumerFactory;
   private DisruptorProvider<T> provider;

   public DisruptorProviderManage(final QueueConsumerFactory<T> consumerFactory, final Integer ringBufferSize) {
      this(consumerFactory, DEFAULT_CONSUMER_SIZE, ringBufferSize);
   }

   public DisruptorProviderManage(final QueueConsumerFactory<T> consumerFactory) {
      this(consumerFactory, DEFAULT_CONSUMER_SIZE, DEFAULT_SIZE);
   }

   public DisruptorProviderManage(final QueueConsumerFactory<T> consumerFactory, final int consumerSize, final int ringBufferSize) {
      this.consumerFactory = consumerFactory;
      this.size = ringBufferSize;
      this.consumerSize = consumerSize;
   }

   public void startup() {
      this.startup(false);
   }

   public void startup(final boolean isOrderly) {
      OrderlyExecutor executor = new OrderlyExecutor(isOrderly, this.consumerSize, this.consumerSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), DisruptorThreadFactory.create("kuma_cloud_disruptor_consumer_", false), new ThreadPoolExecutor.AbortPolicy());
      int newConsumerSize = this.consumerSize;
      EventFactory<DataEvent<T>> eventFactory;
      if (isOrderly) {
         newConsumerSize = 1;
         eventFactory = new OrderlyDisruptorEventFactory<DataEvent<T>>();
      } else {
         eventFactory = new DisruptorEventFactory<DataEvent<T>>();
      }

      Disruptor<DataEvent<T>> disruptor = new Disruptor(eventFactory, this.size, DisruptorThreadFactory.create("kuma_disruptor_provider_" + this.consumerFactory.fixName(), false), ProducerType.MULTI, new BlockingWaitStrategy());
      QueueConsumer<T>[] consumers = new QueueConsumer[newConsumerSize];

      for(int i = 0; i < newConsumerSize; ++i) {
         consumers[i] = new QueueConsumer(executor, this.consumerFactory);
      }

      disruptor.handleEventsWith(consumers);
      disruptor.setDefaultExceptionHandler(new IgnoreExceptionHandler());
      disruptor.start();
      RingBuffer<DataEvent<T>> ringBuffer = disruptor.getRingBuffer();
      this.provider = new DisruptorProvider<T>(ringBuffer, disruptor, isOrderly);
   }

   public DisruptorProvider<T> getProvider() {
      return this.provider;
   }
}
