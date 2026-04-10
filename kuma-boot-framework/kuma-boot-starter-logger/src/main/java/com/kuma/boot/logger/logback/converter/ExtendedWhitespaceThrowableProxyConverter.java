package com.kuma.boot.logger.logback.converter;

import ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.CoreConstants;

public class ExtendedWhitespaceThrowableProxyConverter extends ExtendedThrowableProxyConverter {
   public ExtendedWhitespaceThrowableProxyConverter() {
   }

   protected String throwableProxyToString(IThrowableProxy tp) {
      String var10000 = CoreConstants.LINE_SEPARATOR;
      return "==>" + var10000 + super.throwableProxyToString(tp) + "<==" + CoreConstants.LINE_SEPARATOR + CoreConstants.LINE_SEPARATOR;
   }
}
