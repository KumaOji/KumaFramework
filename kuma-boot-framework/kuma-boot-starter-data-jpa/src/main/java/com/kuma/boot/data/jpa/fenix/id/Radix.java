package com.kuma.boot.data.jpa.fenix.id;

import java.util.HashMap;
import java.util.Map;

public final class Radix {
   private static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
   private static final int RADIX_10 = 10;
   static final int RADIX_62;
   private static final Map<Character, Integer> digitMap;

   public Radix() {
   }

   public static String toString(long i, int radix) {
      if (radix < 2 || radix > RADIX_62) {
         radix = 10;
      }

      if (radix == 10) {
         return Long.toString(i);
      } else {
         boolean negative = i < 0L;
         if (!negative) {
            i = -i;
         }

         int size = 65;
         int charPos = 64;

         char[] buf;
         for(buf = new char[65]; i <= (long)(-radix); i /= (long)radix) {
            buf[charPos--] = DIGITS[(int)(-(i % (long)radix))];
         }

         buf[charPos] = DIGITS[(int)(-i)];
         if (negative) {
            --charPos;
            buf[charPos] = '-';
         }

         return new String(buf, charPos, 65 - charPos);
      }
   }

   private static NumberFormatException throwNumberFormatException(String s) {
      return new NumberFormatException("For input string: \"" + s + "\"");
   }

   public static long toNumber(String s, int radix) {
      if (s == null) {
         throw new NumberFormatException("null");
      } else if (radix < 2) {
         throw new NumberFormatException("radix " + radix + " less than Numbers.MIN_RADIX (2).");
      } else if (radix > RADIX_62) {
         throw new NumberFormatException("radix " + radix + " greater than Numbers.MAX_RADIX (62).");
      } else {
         int len = s.length();
         if (len == 0) {
            throw throwNumberFormatException(s);
         } else {
            boolean negative = false;
            int i = 0;
            long limit = -9223372036854775807L;
            char firstChar = s.charAt(0);
            if (firstChar < '0') {
               if (firstChar == '-') {
                  negative = true;
                  limit = Long.MIN_VALUE;
               } else if (firstChar != '+') {
                  throw throwNumberFormatException(s);
               }

               if (len == 1) {
                  throw throwNumberFormatException(s);
               }

               ++i;
            }

            long multmin = limit / (long)radix;

            long result;
            Integer digit;
            for(result = 0L; i < len; result -= (long)digit) {
               digit = (Integer)digitMap.get(s.charAt(i++));
               if (digit == null || digit < 0 || result < multmin) {
                  throw throwNumberFormatException(s);
               }

               result *= (long)radix;
               if (result < limit + (long)digit) {
                  throw throwNumberFormatException(s);
               }
            }

            return negative ? result : -result;
         }
      }
   }

   public static String digits(long val, int digits) {
      long hi = 1L << digits * 4;
      return toString(hi | val & hi - 1L, RADIX_62).substring(1);
   }

   static {
      RADIX_62 = DIGITS.length;
      digitMap = new HashMap();
      int i = 0;

      for(int len = DIGITS.length; i < len; ++i) {
         digitMap.put(DIGITS[i], i);
      }

   }
}
