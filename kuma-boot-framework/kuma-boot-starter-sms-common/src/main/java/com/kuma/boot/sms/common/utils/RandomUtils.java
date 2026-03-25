package com.kuma.boot.sms.common.utils;

import java.util.concurrent.ThreadLocalRandom;
import org.jspecify.annotations.Nullable;

public final class RandomUtils {
   public static final char[] CHAR_LIST = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

   private RandomUtils() {
   }

   public static String nextString(final int length) {
      return nextString(length, CHAR_LIST);
   }

   public static String nextString(final int length, final @Nullable char[] chars) {
      if (length <= 0) {
         return "";
      } else {
         char[] nowChars = chars;
         if (chars == null || chars.length == 0) {
            nowChars = CHAR_LIST;
         }

         char[] list = new char[length];
         ThreadLocalRandom random = ThreadLocalRandom.current();

         for(int i = 0; i < list.length; ++i) {
            list[i] = nowChars[random.nextInt(nowChars.length)];
         }

         return new String(list);
      }
   }
}
