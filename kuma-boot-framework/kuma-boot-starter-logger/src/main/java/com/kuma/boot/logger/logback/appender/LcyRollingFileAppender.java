package com.kuma.boot.logger.logback.appender;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;

public class LcyRollingFileAppender<E> extends RollingFileAppender<E> {
   public LcyRollingFileAppender() {
   }

   protected void subAppend(E event) {
      DesensitizationAppender appender = new DesensitizationAppender();
      appender.operation((LoggingEvent)event);
      super.subAppend(event);
   }
}
