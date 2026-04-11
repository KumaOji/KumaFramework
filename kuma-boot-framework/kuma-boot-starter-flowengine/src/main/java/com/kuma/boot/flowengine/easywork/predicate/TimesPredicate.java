package com.kuma.boot.flowengine.easywork.predicate;

import com.kuma.boot.flowengine.easywork.report.WorkReport;
import java.util.concurrent.atomic.AtomicInteger;

public class TimesPredicate implements WorkReportPredicate {
   private final int times;
   private final AtomicInteger counter = new AtomicInteger();

   public TimesPredicate(int times) {
      this.times = times;
   }

   public boolean apply(WorkReport workReport) {
      return this.counter.incrementAndGet() == this.times;
   }

   public static TimesPredicate times(int times) {
      return new TimesPredicate(times);
   }
}
