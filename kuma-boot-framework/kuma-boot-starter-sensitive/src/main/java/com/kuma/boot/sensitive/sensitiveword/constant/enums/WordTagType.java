package com.kuma.boot.sensitive.sensitiveword.constant.enums;

public enum WordTagType {
   ZHENGZHI("0", "政治"),
   DUPIN("1", "毒品"),
   SEQING("2", "色情"),
   DUBO("3", "赌博"),
   FANZUI("4", "违法犯罪");

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
