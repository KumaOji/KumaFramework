package com.kuma.boot.sms.common.utils;

import java.util.Arrays;
import java.util.Collection;

public final class StringUtils {
   public static final String EMPTY = "";

   private StringUtils() {
   }

   public static boolean isBlank(final CharSequence cs) {
      int strLen;
      if (cs != null && (strLen = cs.length()) != 0) {
         for(int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(cs.charAt(i))) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isNotBlank(final CharSequence cs) {
      return !isBlank(cs);
   }

   public static boolean isAnyBlank(final CharSequence... css) {
      return css != null && css.length > 0 && Arrays.stream(css).anyMatch(StringUtils::isBlank);
   }

   public static String trimToNull(final String str) {
      if (str == null) {
         return null;
      } else {
         String ts = str.trim();
         return ts.length() == 0 ? null : ts;
      }
   }

   public static String join(Collection collection, String separator) {
      return collection != null && !collection.isEmpty() ? join(collection.toArray(), separator) : "";
   }

   public static String join(Object[] array, String separator) {
      return array == null ? null : join(array, separator, 0, array.length);
   }

   public static String join(Object[] array, String separator, int startIndex, int endIndex) {
      if (array == null) {
         return null;
      } else {
         int noOfItems = endIndex - startIndex;
         if (noOfItems <= 0) {
            return "";
         } else {
            if (separator == null) {
               separator = "";
            }

            StringBuilder buf = new StringBuilder(noOfItems * 16);

            for(int i = startIndex; i < endIndex; ++i) {
               if (i > startIndex) {
                  buf.append(separator);
               }

               if (array[i] != null) {
                  buf.append(array[i]);
               }
            }

            return buf.toString();
         }
      }
   }
}
