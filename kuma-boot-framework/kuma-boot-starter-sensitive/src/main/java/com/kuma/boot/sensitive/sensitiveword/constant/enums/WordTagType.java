package com.kuma.boot.sensitive.sensitiveword.constant.enums;

public enum WordTagType {
   ZHENGZHI("0", "\u653f\u6cbb"),
   DUPIN("1", "\u6bd2\u54c1"),
   SEQING("2", "\u8272\u60c5"),
   DUBO("3", "\u8d4c\u535a"),
   FANZUI("4", "\u8fdd\u6cd5\u72af\u7f6a");

   private final String code;
   private final String desc;

   private WordTagType(String code, String desc) {
      this.code = code;
      this.desc = desc;
   }

   public String getCode() {
      return this.code;
   }

   public String getDesc() {
      return this.desc;
   }

   public static String getDescByCode(final String code) {
      for(WordTagType tagType : values()) {
         if (tagType.code.equals(code)) {
            return tagType.desc;
         }
      }

      return code;
   }

   // $FF: synthetic method
   private static WordTagType[] $values() {
      return new WordTagType[]{ZHENGZHI, DUPIN, SEQING, DUBO, FANZUI};
   }
}
