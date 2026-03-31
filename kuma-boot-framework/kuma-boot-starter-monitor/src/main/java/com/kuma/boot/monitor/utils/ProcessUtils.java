package com.kuma.boot.monitor.utils;

import com.kuma.boot.common.utils.log.LogUtils;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class ProcessUtils {
   public ProcessUtils() {
   }

   public static String execCmd(String cmd) {
      if (isWinOs()) {
         return "-1";
      } else {
         try {
            return execCmd(cmd, (File)null);
         } catch (Exception var2) {
            return "-2";
         }
      }
   }

   public static boolean isWinOs() {
      String os = System.getProperty("os.name");
      return os.toLowerCase().startsWith("win");
   }

   public static String getProcessID() {
      RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
      return runtimeMXBean.getName().split("@")[0];
   }

   public static String execCmd(String cmd, File dir) {
      StringBuilder result = new StringBuilder();
      Process process = null;
      BufferedReader bufferIn = null;

      try {
         String[] commond = new String[]{"sh", "-c", cmd};
         process = Runtime.getRuntime().exec(commond, (String[])null, dir);
         process.waitFor(3L, TimeUnit.SECONDS);
         bufferIn = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));

         String line;
         while((line = bufferIn.readLine()) != null) {
            result.append(line).append('\n');
         }

         String var7 = result.toString();
         return var7;
      } catch (Exception e) {
         LogUtils.error("kuma-boot-starter-monitor", new Object[]{"execCmd", e});
      } finally {
         closeStream(bufferIn);
         if (process != null) {
            process.destroy();
         }

      }

      return "-3";
   }

   private static void closeStream(Closeable stream) {
      if (stream != null) {
         try {
            stream.close();
         } catch (Exception e) {
            LogUtils.error(e);
         }
      }

   }
}
