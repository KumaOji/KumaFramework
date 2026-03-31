package com.kuma.boot.monitor.monitor.registry.seconds;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import org.HdrHistogram.Histogram;

public class SourceData {
   private LongAdder counter = new LongAdder();
   private Histogram histogram;

   public SourceData() {
      this.histogram = new Histogram(1L, TimeUnit.MINUTES.toMillis(1L), 2);
   }

   public void record(double count, long time) {
      this.counter.add((long)count);
      if (time > 0L) {
         try {
            this.histogram.recordValue(time);
         } catch (Exception e) {
            LogUtils.error("SourceStatistician.record error,time:{}", new Object[]{time, e});
         }
      }

   }

   public long getCounter() {
      return this.counter.sum();
   }

   public long getTP99() {
      return this.histogram.getValueAtPercentile((double)99.0F);
   }

   public long getTP90() {
      return this.histogram.getValueAtPercentile((double)90.0F);
   }

   public long getTP50() {
      return this.histogram.getValueAtPercentile((double)50.0F);
   }

   public long getMax() {
      return this.histogram.getMaxValue();
   }
}
