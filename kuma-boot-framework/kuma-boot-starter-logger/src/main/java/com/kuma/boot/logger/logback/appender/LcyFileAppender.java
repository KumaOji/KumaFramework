package com.kuma.boot.logger.logback.appender;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.FileAppender;

public class LcyFileAppender<E> extends FileAppender<E> {
   public LcyFileAppender() {
   }

   protected void subAppend(E event) {
      DesensitizationAppender appender = new DesensitizationAppender();
      appender.operation((LoggingEvent)event);
      super.subAppend(event);
   }
}
