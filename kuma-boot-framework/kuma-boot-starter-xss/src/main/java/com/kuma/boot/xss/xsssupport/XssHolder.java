package com.kuma.boot.xss.xsssupport;

import java.util.Objects;
import org.springframework.util.ObjectUtils;

public class XssHolder {
   private static final ThreadLocal<XssCleanIgnore> TL = new ThreadLocal();

   public XssHolder() {
   }

   public static boolean isEnabled() {
      return Objects.isNull(TL.get());
   }

   public static boolean isIgnore(String name) {
      XssCleanIgnore cleanIgnore = (XssCleanIgnore)TL.get();
      if (cleanIgnore == null) {
         return false;
      } else {
         String[] ignoreArray = cleanIgnore.value();
         return ObjectUtils.containsElement(ignoreArray, name);
      }
   }

   public static void setIgnore(XssCleanIgnore xssCleanIgnore) {
      TL.set(xssCleanIgnore);
   }

   public static void remove() {
      TL.remove();
   }
}
