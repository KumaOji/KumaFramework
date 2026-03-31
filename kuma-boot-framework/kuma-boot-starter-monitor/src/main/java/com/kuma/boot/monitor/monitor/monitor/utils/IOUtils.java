package com.kuma.boot.monitor.monitor.monitor.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {
   public IOUtils() {
   }

   public static void close(Closeable closeable) {
      if (closeable != null) {
         try {
            closeable.close();
         } catch (IOException var2) {
         }
      }

   }
}
