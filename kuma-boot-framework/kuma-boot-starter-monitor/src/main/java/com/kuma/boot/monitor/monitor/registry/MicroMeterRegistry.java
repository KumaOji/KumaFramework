package com.kuma.boot.monitor.monitor.registry;

import com.kuma.boot.monitor.monitor.monitor.utils.CacheMap;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class MicroMeterRegistry implements Registry {
   @Resource
   private MeterRegistry registry;
   private static final double[] percentiles = new double[]{(double)0.5F, 0.9, 0.99};
   private final Map<String, Map<String, Timer>> timerTagMap = new CacheMap<String, Map<String, Timer>>((k) -> new CacheMap((t) -> this.createTimer(k, t)));
   private final Map<String, Map<String, Counter>> counterTagMap = new CacheMap<String, Map<String, Counter>>((k) -> new CacheMap((t) -> this.createCounter(k, t)));

   public MicroMeterRegistry() {
   }

   public Timer get(String key, String tag) {
      if (tag == null) {
         tag = "";
      }

      return (Timer)((Map)this.timerTagMap.get(key)).get(tag);
   }

   private Timer createTimer(String key, String tag) {
      return Objects.equals(tag, "") ? Timer.builder(key).publishPercentiles(percentiles).register(this.registry) : Timer.builder(key).tags(new String[]{"tag", tag}).publishPercentiles(percentiles).register(this.registry);
   }

   public void record(String key, String tag, long time) {
      Timer timer = this.get(key, tag);
      timer.record(time, TimeUnit.MILLISECONDS);
   }

   public void count(String key, String tag, double count) {
      Counter counter = (Counter)((Map)this.counterTagMap.get(key)).get(tag);
      counter.increment(count);
   }

   private Counter createCounter(String key, String tag) {
      return Objects.equals(tag, "") ? Counter.builder(key).register(this.registry) : Counter.builder(key).tag("tag", tag).register(this.registry);
   }
}
