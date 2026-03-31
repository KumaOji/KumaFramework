package com.kuma.boot.monitor.alarm.core.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpUtil {
   private static final Logger log = LoggerFactory.getLogger(IpUtil.class);
   public static final String DEFAULT_IP = "127.0.0.1";
   private static String ip = null;

   public IpUtil() {
   }

   public static String getLocalIpByNetCard() {
      try {
         Enumeration e = NetworkInterface.getNetworkInterfaces();

         while(e.hasMoreElements()) {
            NetworkInterface item = (NetworkInterface)e.nextElement();

            for(InterfaceAddress address : item.getInterfaceAddresses()) {
               if (!item.isLoopback() && item.isUp() && address.getAddress() instanceof Inet4Address) {
                  return ((Inet4Address)address.getAddress()).getHostAddress();
               }
            }
         }

         return InetAddress.getLocalHost().getHostAddress();
      } catch (SocketException | UnknownHostException var4) {
         throw new RuntimeException(var4);
      }
   }

   public static String getLocalIp() {
      if (ip == null) {
         try {
            ip = getLocalIpByNetCard();
         } catch (Exception var1) {
            log.error("get local server ip error!");
            ip = "127.0.0.1";
         }
      }

      return ip;
   }
}
