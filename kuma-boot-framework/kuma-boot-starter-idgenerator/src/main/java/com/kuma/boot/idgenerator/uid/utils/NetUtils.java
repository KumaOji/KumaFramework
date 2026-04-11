package com.kuma.boot.idgenerator.uid.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public abstract class NetUtils {
   public static InetAddress localAddress;

   public NetUtils() {
   }

   public static InetAddress getLocalInetAddress() throws SocketException {
      Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces();

      while(enu.hasMoreElements()) {
         NetworkInterface ni = (NetworkInterface)enu.nextElement();
         if (!ni.isLoopback()) {
            Enumeration<InetAddress> addressEnumeration = ni.getInetAddresses();

            while(addressEnumeration.hasMoreElements()) {
               InetAddress address = (InetAddress)addressEnumeration.nextElement();
               if (!address.isLinkLocalAddress() && !address.isLoopbackAddress() && !address.isAnyLocalAddress()) {
                  return address;
               }
            }
         }
      }

      throw new RuntimeException("No validated local address!");
   }

   public static String getLocalAddress() {
      return localAddress.getHostAddress();
   }

   static {
      try {
         localAddress = getLocalInetAddress();
      } catch (SocketException var1) {
         throw new RuntimeException("fail to get local ip.");
      }
   }
}
