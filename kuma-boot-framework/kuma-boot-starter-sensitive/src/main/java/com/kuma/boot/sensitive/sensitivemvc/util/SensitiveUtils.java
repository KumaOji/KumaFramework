package com.kuma.boot.sensitive.sensitivemvc.util;

import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.util.StringUtils;

public class SensitiveUtils {
   public SensitiveUtils() {
   }

   public static String chineseName(String fullName, char replacer) {
      return !StringUtils.hasLength(fullName) ? "" : CharSequenceUtil.replaceByCodePoint(fullName, 1, fullName.length(), replacer);
   }

   public static String idCardNum(String idCardNum, int front, int end, char replacer) {
      if (!StringUtils.hasLength(idCardNum)) {
         return "";
      } else if (front + end > idCardNum.length()) {
         return "";
      } else {
         return front >= 0 && end >= 0 ? CharSequenceUtil.replaceByCodePoint(idCardNum, front, idCardNum.length() - end, replacer) : "";
      }
   }

   public static String fixedPhone(String num, char replacer) {
      return !StringUtils.hasLength(num) ? "" : CharSequenceUtil.replaceByCodePoint(num, 4, num.length() - 2, replacer);
   }

   public static String mobilePhone(String num, char replacer) {
      return !StringUtils.hasLength(num) ? "" : CharSequenceUtil.replaceByCodePoint(num, 3, num.length() - 4, replacer);
   }

   public static String address(String address, int sensitiveSize, char replacer) {
      if (!StringUtils.hasLength(address)) {
         return "";
      } else {
         int length = address.length();
         return CharSequenceUtil.replaceByCodePoint(address, length - sensitiveSize, length, replacer);
      }
   }

   public static String email(String email, char replacer) {
      if (!StringUtils.hasLength(email)) {
         return "";
      } else {
         int index = CharSequenceUtil.indexOf(email, '@');
         return index <= 1 ? email : CharSequenceUtil.replaceByCodePoint(email, 1, index, replacer);
      }
   }

   public static String password(String password, char replacer) {
      return !StringUtils.hasLength(password) ? "" : CharSequenceUtil.repeat(replacer, password.length());
   }

   public static String carLicense(String carLicense, char replacer) {
      if (!StringUtils.hasLength(carLicense)) {
         return "";
      } else if (carLicense.length() == 7) {
         return CharSequenceUtil.replaceByCodePoint(carLicense, 3, 6, replacer);
      } else {
         return carLicense.length() == 8 ? CharSequenceUtil.replaceByCodePoint(carLicense, 3, 7, replacer) : carLicense;
      }
   }

   public static String bankCard(String bankCardNo, char replacer) {
      if (!StringUtils.hasLength(bankCardNo)) {
         return bankCardNo;
      } else {
         String trimBankCardNo = bankCardNo.trim();
         if (trimBankCardNo.length() < 9) {
            return trimBankCardNo;
         } else {
            int length = trimBankCardNo.length();
            int midLength = length - 8;
            StringBuilder buf = new StringBuilder();
            buf.append(trimBankCardNo, 0, 4);

            for(int i = 0; i < midLength; ++i) {
               if (i % 4 == 0) {
                  buf.append(' ');
               }

               buf.append(replacer);
            }

            buf.append(' ').append(trimBankCardNo, length - 4, length);
            return buf.toString();
         }
      }
   }
}
