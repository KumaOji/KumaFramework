package com.kuma.boot.logger.logback.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.rahulsinghai.logback.kafka.KafkaAppenderConfig;
import com.github.rahulsinghai.logback.kafka.delivery.FailedDeliveryCallback;
import com.google.common.base.Stopwatch;
import com.kuma.boot.common.utils.log.LogUtils;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.ByteArraySerializer;

public class StandardKafkaAppender<E> extends KafkaAppenderConfig<E> {
   private static final int THRESHOLD = 1000;
   private final Stopwatch currentStopwatch = Stopwatch.createStarted();
   private final Stopwatch lastSuccessStopwatch = Stopwatch.createStarted();
   private final Stopwatch lastErrorStopwatch = Stopwatch.createStarted();
   private final AtomicLong sendErrorNum = new AtomicLong(0L);
   private final AtomicLong msgErrorNum = new AtomicLong(0L);
   private final AtomicLong sendSuccessNum = new AtomicLong(0L);
   private static final String KAFKA_LOGGER_PREFIX = KafkaProducer.class.getPackage().getName().replaceFirst("\\.producer$", "");
   private final AppenderAttachableImpl<E> aai = new AppenderAttachableImpl();
   private final ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue();
   private final FailedDeliveryCallback<E> failedDeliveryCallback = new FailedDeliveryCallback<E>() {
      {
         Objects.requireNonNull(StandardKafkaAppender.this);
      }

      public void onFailedDelivery(E evt, Throwable throwable) {
         StandardKafkaAppender.this.aai.appendLoopOnAppenders(evt);
         long andIncrement = StandardKafkaAppender.this.sendErrorNum.getAndIncrement();
         if (andIncrement > 0L && andIncrement % 1000L == 0L) {
            StandardKafkaAppender.this.errorLog(andIncrement, "\u7cfb\u7edf\u65e5\u5fd7\u6d88\u606f\u53d1\u9001\u5931\u8d25");
         }

      }
   };
   private StandardKafkaAppender<E>.LazyProducer lazyProducer = null;

   public StandardKafkaAppender() {
      this.addProducerConfigValue("key.serializer", ByteArraySerializer.class.getName());
      this.addProducerConfigValue("value.serializer", ByteArraySerializer.class.getName());
   }

   public void doAppend(E e) {
      this.ensureDeferredAppends();
      if (e instanceof ILoggingEvent && ((ILoggingEvent)e).getLoggerName().startsWith(KAFKA_LOGGER_PREFIX)) {
         this.deferAppend(e);
      } else {
         super.doAppend(e);
      }

   }

   public void start() {
      if (this.checkPrerequisites()) {
         if (this.partition != null && this.partition < 0) {
            this.partition = null;
         }

         this.lazyProducer = new LazyProducer();
         super.start();
      }

   }

   public void stop() {
      super.stop();
      if (this.lazyProducer != null && this.lazyProducer.isInitialized()) {
         try {
            this.lazyProducer.get().close();
         } catch (KafkaException var2) {
            this.addWarn("Failed to shut down kafka producer: " + var2.getMessage(), var2);
         }

         this.lazyProducer = null;
      }

   }

   public void addAppender(Appender<E> newAppender) {
      this.aai.addAppender(newAppender);
   }

   public Iterator<Appender<E>> iteratorForAppenders() {
      return this.aai.iteratorForAppenders();
   }

   public Appender<E> getAppender(String name) {
      return this.aai.getAppender(name);
   }

   public boolean isAttached(Appender<E> appender) {
      return this.aai.isAttached(appender);
   }

   public void detachAndStopAllAppenders() {
      this.aai.detachAndStopAllAppenders();
   }

   public boolean detachAppender(Appender<E> appender) {
      return this.aai.detachAppender(appender);
   }

   public boolean detachAppender(String name) {
      return this.aai.detachAppender(name);
   }

   protected void append(E e) {
      byte[] payload = this.encoder.encode(e);
      String s = new String(payload);

      JSONObject jsonObject;
      try {
         jsonObject = JSONUtil.parseObj(s.replace("[", "#").replace("]", "#").replace("\n", ""));
      } catch (Exception var12) {
         long andIncrement = this.msgErrorNum.getAndIncrement();
         if (andIncrement > 0L && andIncrement % 1000L == 0L) {
            this.errorLog(andIncrement, "\u7cfb\u7edf\u65e5\u5fd7\u6d88\u606f\u5904\u7406\u5931\u8d25");
         }

         return;
      }

      jsonObject.set("ctime", String.valueOf(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()));
      byte[] key = this.keyingStrategy.createKey(e);
      Long timestamp = this.isAppendTimestamp() ? this.getTimestamp(e) : null;
      ProducerRecord<byte[], byte[]> record = new ProducerRecord(this.topic, this.partition, timestamp, key, payload);
      Producer<byte[], byte[]> producer = this.lazyProducer.get();
      if (producer != null) {
         try {
            this.deliveryStrategy.send(this.lazyProducer.get(), record, e, this.failedDeliveryCallback);
            long andIncrement = this.sendSuccessNum.getAndIncrement();
            if (andIncrement > 0L && andIncrement % 1000L == 0L) {
               this.successLog(andIncrement);
            }
         } catch (Exception var13) {
            long andIncrement = this.sendErrorNum.getAndIncrement();
            if (andIncrement > 0L && andIncrement % 1000L == 0L) {
               this.errorLog(andIncrement, "\u7cfb\u7edf\u65e5\u5fd7\u6d88\u606f\u53d1\u9001\u5931\u8d25");
            }
         }
      } else {
         this.sendErrorNum.getAndIncrement();
         LogUtils.error("kafka producer not init", new Object[0]);
         this.failedDeliveryCallback.onFailedDelivery(e, (Throwable)null);
      }

   }

   protected void successLog(long num) {
      long hour = this.currentStopwatch.elapsed(TimeUnit.HOURS);
      long minute = this.currentStopwatch.elapsed(TimeUnit.MINUTES);
      long seconds = this.currentStopwatch.elapsed(TimeUnit.SECONDS);
      long lastSeconds = this.lastSuccessStopwatch.elapsed(TimeUnit.SECONDS);
      long lastMinute = this.lastSuccessStopwatch.elapsed(TimeUnit.MINUTES);
      long lastHour = this.lastSuccessStopwatch.elapsed(TimeUnit.HOURS);
      LogUtils.info("KafkaAppender [{}\u5df2\u8fbe {}\u6761 \u5171\u7528\u65f6{}\u79d2 {}\u5206 {}\u5c0f\u65f6, \u6700\u8fd1\u4e00\u6b21\u7528\u65f6{}\u79d2 {}\u5206 {}\u5c0f\u65f6]", new Object[]{"\u7cfb\u7edf\u65e5\u5fd7\u6d88\u606f\u53d1\u9001\u6210\u529f", num, seconds, minute, hour, lastSeconds, lastMinute, lastHour});
      this.lastSuccessStopwatch.reset().start();
   }

   protected void errorLog(long num, String msg) {
      long hour = this.currentStopwatch.elapsed(TimeUnit.HOURS);
      long minute = this.currentStopwatch.elapsed(TimeUnit.MINUTES);
      long seconds = this.currentStopwatch.elapsed(TimeUnit.SECONDS);
      long lastSeconds = this.lastErrorStopwatch.elapsed(TimeUnit.SECONDS);
      long lastMinute = this.lastErrorStopwatch.elapsed(TimeUnit.MINUTES);
      long lastHour = this.lastErrorStopwatch.elapsed(TimeUnit.HOURS);
      LogUtils.error("KafkaAppender [{}\u5df2\u8fbe {}\u6761 \u5171\u7528\u65f6{}\u79d2 {}\u5206 {}\u5c0f\u65f6, \u6700\u8fd1\u4e00\u6b21\u7528\u65f6{}\u79d2 {}\u5206 {}\u5c0f\u65f6]", new Object[]{msg, num, seconds, minute, hour, lastSeconds, lastMinute, lastHour});
      this.lastErrorStopwatch.reset().start();
   }

   protected Long getTimestamp(E e) {
      return e instanceof ILoggingEvent ? ((ILoggingEvent)e).getTimeStamp() : System.currentTimeMillis();
   }

   protected Producer<byte[], byte[]> createProducer() {
      return new KafkaProducer(new HashMap(this.producerConfig));
   }

   private void deferAppend(E event) {
      this.queue.add(event);
   }

   private void ensureDeferredAppends() {
      Object event;
      while((event = this.queue.poll()) != null) {
         super.doAppend(event);
      }

   }

   private class LazyProducer {
      private volatile Producer<byte[], byte[]> producer;

      private LazyProducer() {
         Objects.requireNonNull(StandardKafkaAppender.this);
         super();
      }

      public Producer<byte[], byte[]> get() {
         Producer<byte[], byte[]> result = this.producer;
         if (result == null) {
            synchronized(this) {
               result = this.producer;
               if (result == null) {
                  this.producer = result = this.initialize();
               }
            }
         }

         return result;
      }

      protected Producer<byte[], byte[]> initialize() {
         Producer<byte[], byte[]> producer = null;

         try {
            producer = StandardKafkaAppender.this.createProducer();
         } catch (Exception var3) {
            StandardKafkaAppender.this.addError("error creating producer", var3);
         }

         return producer;
      }

      public boolean isInitialized() {
         return this.producer != null;
      }
   }
}
