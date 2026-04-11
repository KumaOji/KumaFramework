package com.kuma.boot.eventbus.disruptor.tmp4.util;

import java.util.Random;

public class NumberUtil {
   private static final Random UTIL = new Random();

   public NumberUtil() {
   }

   public static int upper2Pow(int n) {
      --n;
      n |= n >>> 1;
      n |= n >>> 2;
      n |= n >>> 4;
      n |= n >>> 8;
      n |= n >>> 16;
      return n + 1;
   }

   public static int randomInt(int min, int max) {
      return UTIL.nextInt(min, max + 1);
   }

   public static int randomIndex(int size) {
      return UTIL.nextInt(0, size);
   }
}
