package com.kuma.boot.monitor.monitor.monitor;

import com.kuma.boot.common.utils.log.LogUtils;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.function.ToDoubleFunction;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Monitor {
   private static RegistryInstanceManager registryInstanceManager;
   public static final String IP;
   @Resource
   private MeterRegistry registry;

   public Monitor() {
   }

   private static InetAddress getAddress() {
      try {
         Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

         while(interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = (NetworkInterface)interfaces.nextElement();
            if (!networkInterface.isLoopback() && !networkInterface.isVirtual() && networkInterface.isUp()) {
               Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
               if (addresses.hasMoreElements()) {
                  return (InetAddress)addresses.nextElement();
               }
            }
         }
      } catch (SocketException e) {
         LogUtils.debug("Error when getting host ip address: <{}>.", new Object[]{e.getMessage()});
      }

      return null;
   }

   @Bean
   MeterRegistryCustomizer<MeterRegistry> configurer() {
      return (registry) -> registry.config().commonTags(new String[]{"host", IP});
   }

   public static TimeContext timer(String key) {
      return timer(key, (String)null);
   }

   public static TimeContext timer(String key, String tag) {
      TimeContext timeContext = new TimeContext();
      timeContext.startTime = System.currentTimeMillis();
      timeContext.key = key;
      timeContext.tag = tag;
      return timeContext;
   }

   public static CountContext counter(String key) {
      return counter(key, (String)null);
   }

   public static CountContext counter(String key, String tag) {
      CountContext context = new CountContext();
      context.key = key;
      context.tag = tag;
      return context;
   }

   public <T> void gauge(String key, @Nullable T obj, ToDoubleFunction<T> f) {
      Gauge.builder(key, obj, f).register(this.registry);
   }

   public <T> void gauge(String key, String tag, @Nullable T obj, ToDoubleFunction<T> f) {
      Gauge.builder(key, obj, f).tag("tag", tag).register(this.registry);
   }

   @Resource
   public void setRegistryInstanceManager(RegistryInstanceManager registryInstanceManager) {
      Monitor.registryInstanceManager = registryInstanceManager;
   }

   static {
      try {
         String tempIp = InetAddress.getLocalHost().getHostAddress();
         if ("127.0.0.1".equals(tempIp)) {
            InetAddress address = getAddress();
            if (address != null) {
               tempIp = address.getHostAddress();
            }
         }

         IP = tempIp;
         LogUtils.info("Monitor Ip:{}", new Object[]{IP});
      } catch (UnknownHostException e) {
         throw new RuntimeException(e);
      }
   }

   public static class CountContext {
      private String key;
      private String tag;

      public CountContext() {
      }

      public void end() {
         this.end((double)1.0F);
      }

      public void end(double num) {
         if (Monitor.registryInstanceManager == null) {
            LogUtils.warn("registryInstanceManager is null,key:{}", new Object[]{this.key});
         } else {
            Monitor.registryInstanceManager.count(this.key, this.tag, num);
         }
      }
   }

   public static class TimeContext {
      private String key;
      private String tag;
      private long startTime;

      public TimeContext() {
      }

      public long end() {
         long endTime = System.currentTimeMillis();
         long totalTime = endTime - this.startTime;
         return this.end(totalTime);
      }

      public long end(long totalTime) {
         if (Monitor.registryInstanceManager == null) {
            LogUtils.warn("registryInstanceManager is null,key:{}", new Object[]{this.key});
            return totalTime;
         } else {
            Monitor.registryInstanceManager.record(this.key, this.tag, totalTime);
            return totalTime;
         }
      }

      public void error() {
         if (Monitor.registryInstanceManager == null) {
            LogUtils.warn("counterInstanceManager is null,key:{}", new Object[]{this.key});
         } else {
            String reaTag = this.tag == null ? this.key : this.key + "_" + this.tag;
            Monitor.registryInstanceManager.count("monitor_key_error", reaTag, (double)1.0F);
         }
      }
   }
}
