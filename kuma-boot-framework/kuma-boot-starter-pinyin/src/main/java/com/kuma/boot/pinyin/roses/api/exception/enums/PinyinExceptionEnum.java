package com.kuma.boot.pinyin.roses.api.exception.enums;

public enum PinyinExceptionEnum {
   PARSE_ERROR("C2201", "\u62fc\u97f3\u8f6c\u5316\u5f02\u5e38\uff0c\u5177\u4f53\u4fe1\u606f\uff1a{}");

   private final String errorCode;
   private final String userTip;

   private PinyinExceptionEnum(String errorCode, String userTip) {
      this.errorCode = errorCode;
      this.userTip = userTip;
   }

   // $FF: synthetic method
   private static PinyinExceptionEnum[] $values() {
      return new PinyinExceptionEnum[]{PARSE_ERROR};
   }
}
