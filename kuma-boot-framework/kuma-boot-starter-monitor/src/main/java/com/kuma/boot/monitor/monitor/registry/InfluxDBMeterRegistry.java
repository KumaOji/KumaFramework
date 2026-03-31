package com.kuma.boot.monitor.monitor.registry;

import com.kuma.boot.monitor.monitor.registry.seconds.SourceStatistician;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InfluxDBMeterRegistry implements Registry {
   @Value("${app.monitor.seconds.keys:[]}")
   private Set<String> secondsKeys = new HashSet();
   @Resource
   private SourceStatistician sourceStatistician;

   public InfluxDBMeterRegistry() {
   }

   public void record(String key, String tag, long time) {
      if (this.secondsKeys.contains(key)) {
         this.sourceStatistician.record(key, tag, (double)1.0F, time);
      }

   }

   public void count(String key, String tag, double count) {
      if (this.secondsKeys.contains(key)) {
         this.sourceStatistician.record(key, tag, count, 0L);
      }

   }
}
