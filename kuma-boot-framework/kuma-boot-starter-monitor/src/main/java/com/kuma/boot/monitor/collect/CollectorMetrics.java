package com.kuma.boot.monitor.collect;

import com.kuma.boot.monitor.model.Report;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.Arrays;
import java.util.List;

public class CollectorMetrics implements MeterBinder {
   private final HealthCheckProvider healthCheckProvider;

   public CollectorMetrics(HealthCheckProvider healthCheckProvider) {
      this.healthCheckProvider = healthCheckProvider;
   }

   public void bindTo(MeterRegistry meterRegistry) {
      Report report = this.healthCheckProvider.getReport(false);
      List<String> keys = report.getKeys();
      keys.forEach((field) -> {
         String[] fields = field.split("\\.");
         List<Tag> tags = Arrays.stream(fields).map((f) -> Tag.of(f, f)).toList();
         Report.ReportItem reportItem = report.getByKey(field);
         if (null != reportItem) {
            Gauge.builder(field.replace("_", "."), this.healthCheckProvider, (healthCheckProvider1) -> {
               Report report1 = healthCheckProvider1.getReport(false);
               Report.ReportItem reportItem1 = report1.getByKey(field);
               if (null != reportItem1) {
                  Object value = reportItem1.getValue();
                  if (value instanceof Number) {
                     Number number = (Number)value;
                     return number.doubleValue();
                  } else {
                     return (double)0.0F;
                  }
               } else {
                  return (double)0.0F;
               }
            }).tags(tags).description(reportItem.getDesc()).baseUnit("ms").register(meterRegistry);
         }

      });
   }
}
