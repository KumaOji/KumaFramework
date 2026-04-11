package com.kuma.boot.sensitive.sensitivelog.logback.layout;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class SensitiveLogbackLayout extends PatternLayout {
   public SensitiveLogbackLayout() {
   }

   public String doLayout(ILoggingEvent event) {
      super.doLayout(event);
      return null;
   }
}
