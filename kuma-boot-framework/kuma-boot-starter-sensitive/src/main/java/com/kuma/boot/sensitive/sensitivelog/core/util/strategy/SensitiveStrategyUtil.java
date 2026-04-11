package com.kuma.boot.sensitive.sensitivelog.core.util.strategy;

import com.kuma.boot.common.utils.lang.StringUtils;

public final class SensitiveStrategyUtil {
   private SensitiveStrategyUtil() {
   }

   public static String password(final String password) {
      return null;
   }

   public static String phone(final String phone) {
      int prefixLength = 3;
      String middle = "****";
      return StringUtils.buildString(phone, "****", 3);
   }

   public static String email(final String email) {
      if (StringUtils.isEmpty(email)) {
         return null;
      } else {
         int prefixLength = 3;
         int atIndex = email.indexOf("@");
         String middle = "****";
         if (atIndex > 0) {
            int middleLength = atIndex - 3;
            middle = StringUtils.repeat("*", middleLength);
         }

         return StringUtils.buildString(email, middle, 3);
      }
   }

   public static String chineseName(final String chineseName) {
      if (StringUtils.isEmpty(chineseName)) {
         return chineseName;
      } else {
         int nameLength = chineseName.length();
         if (1 == nameLength) {
            return chineseName;
         } else if (2 == nameLength) {
            return "*" + chineseName.charAt(1);
         } else {
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append(chineseName.charAt(0));

            for(int i = 0; i < nameLength - 2; ++i) {
               stringBuffer.append("*");
            }

            stringBuffer.append(chineseName.charAt(nameLength - 1));
            return stringBuffer.toString();
         }
      }
   }

   public static String cardId(final String cardId) {
      int prefixLength = 6;
      String middle = "**********";
      return StringUtils.buildString(cardId, "**********", 6);
   }
}
