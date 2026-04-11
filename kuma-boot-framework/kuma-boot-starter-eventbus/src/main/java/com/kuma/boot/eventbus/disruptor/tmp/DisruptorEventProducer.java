package com.kuma.boot.eventbus.disruptor.tmp;

import com.lmax.disruptor.RingBuffer;
import com.kuma.boot.eventbus.disruptor.tmp.support.DisruptorEventWrapper;
import com.kuma.boot.eventbus.disruptor.tmp.support.SpringDisruptor;
import java.util.Arrays;
import java.util.List;

public class DisruptorEventProducer<T> {
   private final RingBuffer<DisruptorEventWrapper<T>> ringBuffer;

   public DisruptorEventProducer(SpringDisruptor<T> disruptor) {
      this.ringBuffer = disruptor.getRingBuffer();
   }

   public final void send(T data) {
      long sequence = this.ringBuffer.next();

      try {
         DisruptorEventWrapper<T> event = (DisruptorEventWrapper)this.ringBuffer.get(sequence);
         event.wrap(data);
      } finally {
         this.ringBuffer.publish(sequence);
      }

   }

   public final void sendBatch(List<T> dataList) {
      int n = dataList.size();
      long hi = this.ringBuffer.next(n);
      long lo = hi - (long)(n - 1);

      try {
         for(int i = 0; i < dataList.size(); ++i) {
            ((DisruptorEventWrapper)this.ringBuffer.get((long)i + lo)).wrap(dataList.get(i));
         }
      } finally {
         this.ringBuffer.publish(lo, hi);
      }

   }

   @SafeVarargs
   public final void sendBatch(T... dataArray) {
      this.sendBatch(Arrays.asList(dataArray));
   }
}
