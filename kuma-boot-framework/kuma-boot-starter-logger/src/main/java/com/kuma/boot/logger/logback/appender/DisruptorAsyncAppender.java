package com.kuma.boot.logger.logback.appender;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.LiteTimeoutBlockingWaitStrategy;
import com.lmax.disruptor.PhasedBackoffWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.TimeoutBlockingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import com.kuma.boot.common.utils.servlet.MdcUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class DisruptorAsyncAppender<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E> {
   int appenderCount = 0;
   Map<String, Appender<E>> appenderMap = new HashMap();
   private RingBuffer<DisruptorAsyncAppender<E>.Attach> ringBuffer;
   private ExecutorService threadPoolExecutor;
   private Integer ringBufferSize = 128;
   private Integer coreSize = 1;
   private WaitStrategy waitStrategy;
   private ThreadRejectStrategy threadRejectStrategy;

   public DisruptorAsyncAppender() {
      this.waitStrategy = DisruptorAsyncAppender.WaitStrategy.YieldingWaitStrategy;
      this.threadRejectStrategy = DisruptorAsyncAppender.ThreadRejectStrategy.CallerRunsPolicy;
   }

   public void setWaitStrategy(WaitStrategy waitStrategy) {
      this.waitStrategy = waitStrategy;
   }

   public void setThreadRejectStrategy(ThreadRejectStrategy threadRejectStrategy) {
      this.threadRejectStrategy = threadRejectStrategy;
   }

   public void setCoreSize(Integer coreSize) {
      this.coreSize = coreSize;
   }

   public void setRingBufferSize(Integer ringBufferSize) {
      this.ringBufferSize = ringBufferSize;
   }

   protected void append(E e) {
      try {
         LoggingEvent event = (LoggingEvent)e;
         event.setMDCPropertyMap(MdcUtils.getCopyOfContextMap());
         String threadName = Thread.currentThread().getName();
         if (!threadName.equals(event.getThreadName())) {
            event.setThreadName(threadName);
         }
      } catch (Exception var5) {
      }

      long sequence = this.ringBuffer.next();
      DisruptorAsyncAppender<E>.Attach attach = (Attach)this.ringBuffer.get(sequence);
      attach.e = e;
      this.ringBuffer.publish(sequence);
   }

   public void start() {
      if (!this.isStarted()) {
         super.start();
         if (this.appenderCount == 0) {
            this.addError("No attached appenders found.");
         }

         EventFactory<DisruptorAsyncAppender<E>.Attach> eventFactory = () -> new Attach();
         this.ringBuffer = RingBuffer.create(ProducerType.SINGLE, eventFactory, this.ringBufferSize, this.waitStrategy.create());
      }

   }

   public void stop() {
      super.stop();
      this.threadPoolExecutor.shutdown();
   }

   public void addAppender(Appender<E> newAppender) {
      this.appenderMap.put(newAppender.getName(), newAppender);
      this.appenderCount = this.appenderMap.size();
   }

   public Iterator<Appender<E>> iteratorForAppenders() {
      return this.appenderMap.values().iterator();
   }

   public Appender<E> getAppender(String s) {
      return (Appender)this.appenderMap.get(s);
   }

   public boolean isAttached(Appender<E> appender) {
      return this.appenderMap.containsValue(appender);
   }

   public void detachAndStopAllAppenders() {
      this.appenderMap.forEach((k, v) -> v.stop());
      this.appenderMap.clear();
   }

   public boolean detachAppender(Appender<E> appender) {
      return this.appenderMap.remove(appender.getName()) != null;
   }

   public boolean detachAppender(String s) {
      return this.appenderMap.remove(s) != null;
   }

   public static enum WaitStrategy {
      BlockingWaitStrategy(BlockingWaitStrategy::new),
      YieldingWaitStrategy(YieldingWaitStrategy::new),
      BusySpinWaitStrategy(BusySpinWaitStrategy::new),
      LiteBlockingWaitStrategy(LiteBlockingWaitStrategy::new),
      LiteTimeoutBlockingWaitStrategy(() -> {
         String waitTimeSeconds = System.getProperty("waitStrategyWaitTimeSeconds");
         Integer seconds = (Integer)Optional.ofNullable(waitTimeSeconds).map(Integer::parseInt).orElse(15);
         return new LiteTimeoutBlockingWaitStrategy((long)seconds, TimeUnit.SECONDS);
      }),
      PhasedBackoffWaitStrategy(() -> {
         String spinTimeout = System.getProperty("waitStrategySpinTimeout");
         Integer spinTimeoutSeconds = (Integer)Optional.ofNullable(spinTimeout).map(Integer::parseInt).orElse(3);
         String yieldTimeout = System.getProperty("waitStrategyYieldTimeout");
         Integer yieldTimeoutSeconds = (Integer)Optional.ofNullable(yieldTimeout).map(Integer::parseInt).orElse(1);
         String fallbackStrategy = (String)Optional.ofNullable(System.getProperty("waitStrategyFallbackStrategy")).orElse("YieldingWaitStrategy");
         return new PhasedBackoffWaitStrategy((long)spinTimeoutSeconds, (long)yieldTimeoutSeconds, TimeUnit.SECONDS, valueOf(fallbackStrategy).create());
      }),
      SleepingWaitStrategy(() -> {
         String retries = System.getProperty("waitStrategyRetries");
         Integer retriesCount = (Integer)Optional.ofNullable(retries).map(Integer::parseInt).orElse(200);
         String sleepTimeNs = System.getProperty("waitStrategySleepTimeNs");
         Integer sleepTimeNsValue = (Integer)Optional.ofNullable(sleepTimeNs).map(Integer::parseInt).orElse(100);
         return new SleepingWaitStrategy(retriesCount, (long)sleepTimeNsValue);
      }),
      TimeoutBlockingWaitStrategy(() -> {
         String waitTimeSeconds = System.getProperty("waitStrategyWaitTimeSeconds");
         Integer seconds = (Integer)Optional.ofNullable(waitTimeSeconds).map(Integer::parseInt).orElse(15);
         return new TimeoutBlockingWaitStrategy((long)seconds, TimeUnit.SECONDS);
      });

      private final Supplier<com.lmax.disruptor.WaitStrategy> supplier;

      private WaitStrategy(Supplier<com.lmax.disruptor.WaitStrategy> supplier) {
         this.supplier = supplier;
      }

      public com.lmax.disruptor.WaitStrategy create() {
         return (com.lmax.disruptor.WaitStrategy)this.supplier.get();
      }

      // $FF: synthetic method
      private static WaitStrategy[] $values() {
         return new WaitStrategy[]{BlockingWaitStrategy, YieldingWaitStrategy, BusySpinWaitStrategy, LiteBlockingWaitStrategy, LiteTimeoutBlockingWaitStrategy, PhasedBackoffWaitStrategy, SleepingWaitStrategy, TimeoutBlockingWaitStrategy};
      }
   }

   public static enum ThreadRejectStrategy {
      AbortPolicy(ThreadPoolExecutor.AbortPolicy::new),
      CallerRunsPolicy(ThreadPoolExecutor.CallerRunsPolicy::new),
      DiscardOldestPolicy(ThreadPoolExecutor.DiscardOldestPolicy::new),
      DiscardPolicy(ThreadPoolExecutor.DiscardPolicy::new);

      private final Supplier<RejectedExecutionHandler> supplier;

      private ThreadRejectStrategy(Supplier<RejectedExecutionHandler> supplier) {
         this.supplier = supplier;
      }

      public RejectedExecutionHandler create() {
         return (RejectedExecutionHandler)this.supplier.get();
      }

      // $FF: synthetic method
      private static ThreadRejectStrategy[] $values() {
         return new ThreadRejectStrategy[]{AbortPolicy, CallerRunsPolicy, DiscardOldestPolicy, DiscardPolicy};
      }
   }

   class Attach {
      private E e;

      Attach() {
         Objects.requireNonNull(DisruptorAsyncAppender.this);
         super();
      }
   }
}
