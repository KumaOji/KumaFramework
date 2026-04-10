package com.kuma.boot.logger.logback.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogbackPatternConverter extends ClassicConverter {
   public LogbackPatternConverter() {
   }

   public String convert(ILoggingEvent iLoggingEvent) {
      return "tid:";
   }
}
