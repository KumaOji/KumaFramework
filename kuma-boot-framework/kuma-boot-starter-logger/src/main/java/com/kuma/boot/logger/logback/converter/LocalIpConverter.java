package com.kuma.boot.logger.logback.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalIpConverter extends ClassicConverter {
   private static String localIp = "0.0.0.0";

   public LocalIpConverter() {
   }

   public String convert(ILoggingEvent event) {
      return localIp;
   }

   static {
      try {
         localIp = InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException var1) {
      }

   }
}
