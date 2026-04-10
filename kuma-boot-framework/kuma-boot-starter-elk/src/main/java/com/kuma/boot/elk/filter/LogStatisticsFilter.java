package com.kuma.boot.elk.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.concurrent.atomic.AtomicLong;

public class LogStatisticsFilter extends AbstractMatcherFilter<ILoggingEvent> {
   public static final int DEFAULT_TIME = 60000;
   private long lastCollectTime = System.currentTimeMillis() / 60000L;
   private static final AtomicLong ERROR_COUNT = new AtomicLong(0L);
   private static final AtomicLong LOG_COUNT = new AtomicLong(0L);

   public LogStatisticsFilter() {
   }

   public FilterReply decide(ILoggingEvent event) {
      LOG_COUNT.incrementAndGet();
      if (event.getLevel().equals(Level.ERROR)) {
         ERROR_COUNT.incrementAndGet();
      }

      if (System.currentTimeMillis() / 60000L > this.lastCollectTime) {
         this.lastCollectTime = System.currentTimeMillis() / 60000L;
         LOG_COUNT.set(0L);
         ERROR_COUNT.set(0L);
      }

      return FilterReply.NEUTRAL;
   }
}
