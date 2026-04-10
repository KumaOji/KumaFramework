package com.kuma.boot.logger.eden;

import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IpConfigUtils {
   private static final String X_FORWARDED_FOR = "X-Forwarded-For";
   private static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
   private static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
   private static final String IP_ADDRESS = getIpAddress((String)null);
   public static final String SUBNET_MASK = "255.255.255.0";

   public IpConfigUtils() {
   }

   public static String getIpAddress() {
      return IP_ADDRESS;
   }

   public static String getIpAddress(String interfaceName) {
      try {
         List<String> ipList = getHostAddress(interfaceName);
         return StringUtils.isNotEmpty(ipList) ? (String)ipList.get(0) : "";
      } catch (Exception var2) {
         return "";
      }
   }

   private static List<String> getHostAddress(String interfaceName) throws SocketException {
      List<String> ipList = new ArrayList(5);
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

      while(interfaces.hasMoreElements()) {
         NetworkInterface networkInterface = (NetworkInterface)interfaces.nextElement();
         Enumeration<InetAddress> allAddress = networkInterface.getInetAddresses();

         while(allAddress.hasMoreElements()) {
            InetAddress address = (InetAddress)allAddress.nextElement();
            if (!address.isLoopbackAddress() && !(address instanceof Inet6Address)) {
               String hostAddress = address.getHostAddress();
               if (interfaceName == null) {
                  ipList.add(hostAddress);
               } else if (networkInterface.getDisplayName().equals(interfaceName)) {
                  ipList.add(hostAddress);
               }
            }
         }
      }

      return ipList;
   }

   public static String parseIpAddress(HttpServletRequest request) {
      String ip = request.getHeader("X-Forwarded-For");
      if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
         ip = request.getHeader("Proxy-Client-IP");
      }

      if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
         ip = request.getHeader("WL-Proxy-Client-IP");
      }

      if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
         ip = request.getRemoteAddr();
      }

      return ip;
   }

   public static String getMacAddress() throws SocketException, UnknownHostException {
      return getMacAddress(InetAddress.getLocalHost());
   }

   public static String getMacAddress(InetAddress inetAddress) throws SocketException {
      byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < mac.length; ++i) {
         if (i != 0) {
            sb.append('-');
         }

         String s = Integer.toHexString(mac[i] & 255);
         sb.append(s.length() == 1 ? "0" + s : s);
      }

      return sb.toString().toUpperCase();
   }

   public static boolean isSameSubnet(String ip1, String ip2, String subnetMask) throws UnknownHostException {
      InetAddress address1 = InetAddress.getByName(ip1);
      InetAddress address2 = InetAddress.getByName(ip2);
      InetAddress subnet = InetAddress.getByName(subnetMask);
      byte[] b1 = address1.getAddress();
      byte[] b2 = address2.getAddress();
      byte[] b3 = subnet.getAddress();

      for(int i = 0; i < b1.length; ++i) {
         if ((b1[i] & b3[i]) != (b2[i] & b3[i])) {
            return false;
         }
      }

      return true;
   }

   public static boolean isSameSubnet(String ip1, String ip2) throws UnknownHostException {
      return isSameSubnet(ip1, ip2, "255.255.255.0");
   }
}
