package com.kuma.boot.sensitive.sensitiveword.constant.enums;

public enum WordTypeEnum {
   WORD("WORD", "\u654f\u611f\u8bcd"),
   EMAIL("EMAIL", "\u90ae\u7bb1"),
   URL("URL", "\u94fe\u63a5"),
   NUM("NUM", "\u6570\u5b57"),
   IPV4("IPV4", "IPv4"),
   DEFAULTS("DEFAULTS", "\u9ed8\u8ba4");

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
