package com.kuma.boot.sensitive.sensitiveword.utils;

public class InnerCharUtils {
   public InnerCharUtils() {
   }

   public static int parseInt(String text) {
      int len = text.length();
      int sum = 0;
      int weight = 1;
      char[] chars = text.toCharArray();

      for(int i = len - 1; i >= 0; --i) {
         int val = getCharInt(chars[i]);
         sum += weight * val;
         weight *= 10;
      }

      return sum;
   }

   public static int getCharInt(final char c) {
      return c - 48;
   }
}
