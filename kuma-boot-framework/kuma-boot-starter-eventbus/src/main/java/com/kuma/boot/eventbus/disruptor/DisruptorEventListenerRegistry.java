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
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;


public class DisruptorEventListenerRegistry implements EventListenerRegistry<EventModel>, AutoCloseable {

   /**
    * disruptor事件处理器
    */
   private Disruptor<EventModel> disruptor;

   final List<EventListener> eventListeners;


   /**
    * RingBuffer的大小
    */
   private final int DEFAULT_RING_SIZE = 1024 * 1024;

   /**
    * 事件工厂
    */
   private EventFactory<EventModel> eventFactory = new EventModelFactory();

   public DisruptorEventListenerRegistry(List<EventListener> eventListeners) {
      this.eventListeners = eventListeners;
   }

   @Override
   public void initRegistryEventListener(List<EventListener> eventConsumerList) {

      disruptor = new Disruptor<>(eventFactory, DEFAULT_RING_SIZE,
              createThreadFactory(),
              //// 单生产者
              ProducerType.SINGLE,
              // 阻塞等待策略
              new BlockingWaitStrategy());

      EventHandler[] dataListener = eventConsumerList.stream().map(param -> {
         EventListener<EventModel> eventModelEventListener = param;
         return eventModelEventListener;
      }).collect(Collectors.toList()).toArray(new EventHandler[eventConsumerList.size()]);
      LogUtils.info("注册服务信息接口：{}", dataListener);

      disruptor.handleEventsWith(dataListener);
      disruptor.start();
   }

   @Override
   public void publish(EventModel param) {
      publishEvent(param);
   }

   public void publishEvent(EventModel... eventModels) {
      Objects.requireNonNull(disruptor, "当前disruptor核心控制器不可以为null");
      Objects.requireNonNull(eventModels, "当前eventModels事件控制器不可以为null");

      // 发布事件
      final RingBuffer<EventModel> ringBuffer = disruptor.getRingBuffer();
      try {
         final List<EventModel> dataList = Arrays.stream(eventModels).collect(Collectors.toList());

         for (EventModel element : dataList) {

            // 请求下一个序号
            long sequence = ringBuffer.next();

            // 获取该序号对应的事件对象
            EventModel event = ringBuffer.get(sequence);
            event.setTopic(element.getTopic());
            event.setEntity(element.getEntity());
            ringBuffer.publish(sequence);
         }
      } catch (Exception e) {
         LogUtils.error("error", e);
      }

   }


   /**
    * 关闭处理机制
    *
    * @throws Exception
    */
   @Override
   public void close() throws Exception {
      if (Objects.nonNull(disruptor)) {
         disruptor.shutdown();
      }

   }

   @PostConstruct
   public void init() {
      LogUtils.info("开始初始化Disruptor事件监听器的组件服务");
      initRegistryEventListener(eventListeners);
      LogUtils.info("完成初始化Disruptor事件监听器的组件服务");
   }

   private static ThreadFactory createThreadFactory() {
      BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
              .namingPattern("eventBus" + "-Disruptor-%d")
              .daemon(true)
              .priority(Thread.NORM_PRIORITY)
              .uncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                 @Override
                 public void uncaughtException(Thread t, Throwable e) {
                    LogUtils.error(String.format("创建线程(%s)异常", t.getName()), e);
                 }
              })
              .build();
      return threadFactory;

   }


}
