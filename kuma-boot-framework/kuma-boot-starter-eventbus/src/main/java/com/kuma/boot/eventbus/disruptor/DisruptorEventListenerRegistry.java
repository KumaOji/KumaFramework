package com.kuma.boot.eventbus.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.EventListener;
import com.kuma.boot.eventbus.EventListenerRegistry;
import com.kuma.boot.eventbus.model.EventModel;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class DisruptorEventListenerRegistry implements EventListenerRegistry<EventModel>, AutoCloseable {
   private Disruptor<EventModel> disruptor;
   final List<EventListener> eventListeners;
   private final int DEFAULT_RING_SIZE = 1048576;
   private EventFactory<EventModel> eventFactory = new EventModelFactory<EventModel>();

   public DisruptorEventListenerRegistry(List<EventListener> eventListeners) {
      this.eventListeners = eventListeners;
   }

   public void initRegistryEventListener(List<EventListener> eventConsumerList) {
      this.disruptor = new Disruptor(this.eventFactory, 1048576, createThreadFactory(), ProducerType.SINGLE, new BlockingWaitStrategy());
      EventHandler[] dataListener = (EventHandler[])((List)eventConsumerList.stream().map((param) -> param).collect(Collectors.toList())).toArray(new EventHandler[eventConsumerList.size()]);
      LogUtils.info("\u6ce8\u518c\u670d\u52a1\u4fe1\u606f\u63a5\u53e3\uff1a{}", dataListener);
      this.disruptor.handleEventsWith(dataListener);
      this.disruptor.start();
   }

   public void publish(EventModel param) {
      this.publishEvent(param);
   }

   public void publishEvent(EventModel... eventModels) {
      Objects.requireNonNull(this.disruptor, "\u5f53\u524ddisruptor\u6838\u5fc3\u63a7\u5236\u5668\u4e0d\u53ef\u4ee5\u4e3anull");
      Objects.requireNonNull(eventModels, "\u5f53\u524deventModels\u4e8b\u4ef6\u63a7\u5236\u5668\u4e0d\u53ef\u4ee5\u4e3anull");
      RingBuffer<EventModel> ringBuffer = this.disruptor.getRingBuffer();

      try {
         for(EventModel element : (List)Arrays.stream(eventModels).collect(Collectors.toList())) {
            long sequence = ringBuffer.next();
            EventModel event = (EventModel)ringBuffer.get(sequence);
            event.setTopic(element.getTopic());
            event.setEntity(element.getEntity());
            ringBuffer.publish(sequence);
         }
      } catch (Exception e) {
         LogUtils.error("error", new Object[]{e});
      }

   }

   public void close() throws Exception {
      if (Objects.nonNull(this.disruptor)) {
         this.disruptor.shutdown();
      }

   }

   @PostConstruct
   public void init() {
      LogUtils.info("\u5f00\u59cb\u521d\u59cb\u5316Disruptor\u4e8b\u4ef6\u76d1\u542c\u5668\u7684\u7ec4\u4ef6\u670d\u52a1", new Object[0]);
      this.initRegistryEventListener(this.eventListeners);
      LogUtils.info("\u5b8c\u6210\u521d\u59cb\u5316Disruptor\u4e8b\u4ef6\u76d1\u542c\u5668\u7684\u7ec4\u4ef6\u670d\u52a1", new Object[0]);
   }

   private static ThreadFactory createThreadFactory() {
      BasicThreadFactory threadFactory = (new BasicThreadFactory.Builder()).namingPattern("eventBus-Disruptor-%d").daemon(true).priority(5).uncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
         public void uncaughtException(Thread t, Throwable e) {
            LogUtils.error(String.format("\u521b\u5efa\u7ebf\u7a0b(%s)\u5f02\u5e38", t.getName()), new Object[]{e});
         }
      }).build();
      return threadFactory;
   }
}
