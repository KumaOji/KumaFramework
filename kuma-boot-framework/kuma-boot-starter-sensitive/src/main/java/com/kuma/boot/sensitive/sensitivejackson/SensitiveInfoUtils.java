package com.kuma.boot.sensitive.sensitivejackson;

import com.kuma.boot.common.utils.lang.StringUtils;

public class SensitiveInfoUtils {
   public SensitiveInfoUtils() {
   }

   public static String chineseName(final String fullName) {
      if (StringUtils.isBlank(fullName)) {
         return "";
      } else {
         String name = StringUtils.left(fullName, 1);
         return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
      }
   }

   public static String chineseName(final String familyName, final String givenName) {
      return !StringUtils.isBlank(familyName) && !StringUtils.isBlank(givenName) ? chineseName(familyName + givenName) : "";
   }

   public static String idCardNum(final String id) {
      return StringUtils.isBlank(id) ? "" : StringUtils.left(id, 3).concat(StringUtils.removePrefix(StringUtils.leftPad(StringUtils.right(id, 3), StringUtils.length(id), "*"), "***"));
   }

   public static String fixedPhone(final String num) {
      return StringUtils.isBlank(num) ? "" : StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*");
   }

   public static String mobilePhone(final String num) {
      return StringUtils.isBlank(num) ? "" : StringUtils.left(num, 2).concat(org.apache.commons.lang3.StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(num, 2), StringUtils.length(num), "*"), "***"));
   }

   public static String address(final String address, final int sensitiveSize) {
      if (StringUtils.isBlank(address)) {
         return "";
      } else {
         int length = StringUtils.length(address);
         return StringUtils.rightPad(StringUtils.left(address, length - sensitiveSize), length, "*");
      }
   }

   public static String email(final String email) {
      if (StringUtils.isBlank(email)) {
         return "";
      } else {
         int index = StringUtils.indexOf(email, '@');
         return index <= 1 ? email : StringUtils.rightPad(StringUtils.left(email, 1), index, "*").concat(StringUtils.mid(email, index, StringUtils.length(email)));
      }
   }

   public static String bankCard(final String cardNum) {
      return StringUtils.isBlank(cardNum) ? "" : StringUtils.left(cardNum, 6).concat(org.apache.commons.lang3.StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum), "*"), "******"));
   }

   public static String cnapsCode(final String code) {
      return StringUtils.isBlank(code) ? "" : StringUtils.rightPad(StringUtils.left(code, 2), StringUtils.length(code), "*");
   }
}
