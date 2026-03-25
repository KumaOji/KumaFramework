package com.kuma.boot.encrypt.encrypt2.format;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.lang.StringUtils;

public class CommonSensitiveProcessor implements SensitiveProcessor {
   public String format(String text, String type) {
      if (StringUtils.isBlank(text)) {
         return text;
      } else {
         switch (type) {
            case "CHINESE_NAME" -> {
               return this.chineseName(text);
            }
            case "ID_CARD" -> {
               return this.idCardNum(text);
            }
            case "FIXED_PHONE" -> {
               return this.fixedPhone(text);
            }
            case "MOBILE_PHONE" -> {
               return this.mobilePhone(text);
            }
            case "ADDRESS" -> {
               return this.address(text, 8);
            }
            case "EMAIL" -> {
               return this.email(text);
            }
            case "BANK_CARD" -> {
               return this.bankCard(text);
            }
            case "PASSWORD" -> {
               return this.password(text);
            }
            case "CARNUMBER" -> {
               return this.carNumber(text);
            }
            case "DEFAULT" -> {
               return defaultType(text);
            }
            default -> {
               return text;
            }
         }
      }
   }

   public static String defaultType(String text) {
      if (StringUtils.isBlank(text)) {
         return "";
      } else if (text.length() == 1) {
         return "*";
      } else if (text.length() == 2) {
         return StringUtils.left(text, 1) + "*";
      } else if (text.length() >= 3) {
         String var10000 = StringUtils.rightPad(StringUtils.left(text, 1), StringUtils.length(text) - 1, "*");
         return var10000 + StringUtils.right(text, 1);
      } else {
         return text;
      }
   }

   private String chineseName(String fullName) {
      if (StrUtil.isBlank(fullName)) {
         return "";
      } else {
         String name = StringUtils.left(fullName, 1);
         return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
      }
   }

   private String idCardNum(String id) {
      if (StringUtils.isBlank(id)) {
         return "";
      } else {
         String num = StringUtils.right(id, 4);
         return StringUtils.leftPad(num, StringUtils.length(id), "*");
      }
   }

   private String fixedPhone(String num) {
      return StringUtils.isBlank(num) ? "" : StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*");
   }

   private String mobilePhone(String num) {
      return StringUtils.isBlank(num) ? "" : StringUtils.left(num, 3).concat(StringUtils.removePrefix(StringUtils.leftPad(StringUtils.right(num, 2), StringUtils.length(num), "*"), "***"));
   }

   private String address(String address, int sensitiveSize) {
      if (StringUtils.isBlank(address)) {
         return "";
      } else {
         int length = StringUtils.length(address);
         return StringUtils.rightPad(StringUtils.left(address, length - sensitiveSize), length, "*");
      }
   }

   private String email(String email) {
      if (StringUtils.isBlank(email)) {
         return "";
      } else {
         int index = StringUtils.indexOf(email, '@');
         return index <= 1 ? email : StringUtils.rightPad(StringUtils.left(email, 1), index, "*").concat(StringUtils.mid(email, index, StringUtils.length(email)));
      }
   }

   private String bankCard(String cardNum) {
      return StringUtils.isBlank(cardNum) ? "" : StringUtils.left(cardNum, 6).concat(org.apache.commons.lang3.StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum), "*"), "******"));
   }

   private String password(String password) {
      if (StringUtils.isBlank(password)) {
         return "";
      } else {
         String pwd = StringUtils.left(password, 0);
         return StringUtils.rightPad(pwd, StringUtils.length(password), "*");
      }
   }

   private String carNumber(String carNumber) {
      return StringUtils.isBlank(carNumber) ? "" : StringUtils.left(carNumber, 2).concat(org.apache.commons.lang3.StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(carNumber, 1), StringUtils.length(carNumber), "*"), "**"));
   }
}
