package com.kuma.boot.logger.logback.appender;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

public class LcyConsoleAppender<E> extends ConsoleAppender<E> {
   public LcyConsoleAppender() {
   }

   protected void subAppend(E event) {
      DesensitizationAppender appender = new DesensitizationAppender();
      appender.operation((LoggingEvent)event);
      super.subAppend(event);
   }
}
