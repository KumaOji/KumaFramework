package com.kuma.boot.logger.logback.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class LocalIpConverterOther extends ClassicConverter {
   public LocalIpConverterOther() {
   }

   public String convert(ILoggingEvent iLoggingEvent) {
      try {
         Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();

         while(allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface)allNetInterfaces.nextElement();
            if (!netInterface.isLoopback() && !netInterface.isVirtual() && netInterface.isUp()) {
               Enumeration<InetAddress> addresses = netInterface.getInetAddresses();

               while(addresses.hasMoreElements()) {
                  InetAddress ip = (InetAddress)addresses.nextElement();
                  if (ip instanceof Inet4Address) {
                     return ip.getHostAddress();
                  }
               }
            }
         }

         return "0.0.0.0";
      } catch (Exception var6) {
         return "0.0.0.0";
      }
   }
}
