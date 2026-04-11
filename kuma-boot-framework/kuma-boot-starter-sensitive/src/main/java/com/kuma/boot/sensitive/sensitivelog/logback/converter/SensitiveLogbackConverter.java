package com.kuma.boot.sensitive.sensitivelog.logback.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class SensitiveLogbackConverter extends ClassicConverter {
   public SensitiveLogbackConverter() {
   }

   public String convert(ILoggingEvent iLoggingEvent) {
      String originalMessage = iLoggingEvent.getFormattedMessage();
      return null;
   }
}
