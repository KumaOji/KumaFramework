package com.kuma.boot.eventbus.disruptor.tmp1.provider;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.kuma.boot.eventbus.disruptor.tmp1.event.DataEvent;
import com.kuma.boot.eventbus.disruptor.tmp1.event.OrderlyDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisruptorProvider<T> {
   private final RingBuffer<DataEvent<T>> ringBuffer;
   private final Disruptor<DataEvent<T>> disruptor;
   private final boolean isOrderly;
   private final EventTranslatorOneArg<DataEvent<T>, T> translatorOneArg = (event, sequence, t) -> event.setData(t);
   private final EventTranslatorTwoArg<DataEvent<T>, T, String> orderlyArg = (event, sequence, t, orderly) -> {
      if (event instanceof OrderlyDataEvent) {
         ((OrderlyDataEvent)event).setHash(orderly);
      }

      event.setData(t);
   };
   private final Logger logger = LoggerFactory.getLogger(DisruptorProvider.class);

   public DisruptorProvider(final RingBuffer<DataEvent<T>> ringBuffer, final Disruptor<DataEvent<T>> disruptor, final boolean isOrderly) {
      this.ringBuffer = ringBuffer;
      this.disruptor = disruptor;
      this.isOrderly = isOrderly;
   }

   public void onData(final T data) {
      if (this.isOrderly) {
         throw new IllegalArgumentException("The current provider is  of orderly type. Please use onOrderlyData() method.");
      } else {
         try {
            this.ringBuffer.publishEvent(this.translatorOneArg, data);
         } catch (Exception ex) {
            this.logger.error("ex", ex);
         }

      }
   }

   public void onOrderlyData(final T data, final String... hashArray) {
      if (!this.isOrderly) {
         throw new IllegalArgumentException("The current provider is not of orderly type. Please use onData() method.");
      } else {
         try {
            String hash = String.join(":", hashArray);
            this.ringBuffer.publishEvent(this.orderlyArg, data, hash);
         } catch (Exception ex) {
            this.logger.error("ex", ex);
         }

      }
   }

   public void shutdown() {
      if (null != this.disruptor) {
         this.disruptor.shutdown();
      }

   }
}
