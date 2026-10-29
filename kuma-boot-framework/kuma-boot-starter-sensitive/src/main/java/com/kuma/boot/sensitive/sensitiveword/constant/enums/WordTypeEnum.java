package com.kuma.boot.sensitive.sensitiveword.constant.enums;

public enum WordTypeEnum {
   WORD("WORD", "敏感词"),
   EMAIL("EMAIL", "邮箱"),
   URL("URL", "链接"),
   NUM("NUM", "数字"),
   IPV4("IPV4", "IPv4"),
   DEFAULTS("DEFAULTS", "默认");

   private final String code;
   private final String desc;

   private WordTypeEnum(String code, String desc) {
      this.code = code;
      this.desc = desc;
   }

   public String getCode() {
      return this.code;
   }

   public String getDesc() {
      return this.desc;
   }

   // $FF: synthetic method
   private static WordTypeEnum[] $values() {
      return new WordTypeEnum[]{WORD, EMAIL, URL, NUM, IPV4, DEFAULTS};
   }
}
