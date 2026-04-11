package com.kuma.boot.sensitive.sensitiveword.utils;

import com.kuma.boot.common.utils.io.FileStreamUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class InnerStreamUtils {
   public InnerStreamUtils() {
   }

   public static List<String> readAllLines(String path) {
      try {
         InputStream inputStream = FileStreamUtils.class.getResourceAsStream(path);

         label45: {
            List var2;
            try {
               if (inputStream != null) {
                  break label45;
               }

               var2 = Collections.emptyList();
            } catch (Throwable var5) {
               if (inputStream != null) {
                  try {
                     inputStream.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (inputStream != null) {
               inputStream.close();
            }

            return var2;
         }

         if (inputStream != null) {
            inputStream.close();
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      return FileStreamUtils.readAllLines(path);
   }
}
