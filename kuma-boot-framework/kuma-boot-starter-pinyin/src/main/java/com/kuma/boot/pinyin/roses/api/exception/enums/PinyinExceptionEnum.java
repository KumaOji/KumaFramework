package com.kuma.boot.pinyin.roses.api.exception.enums;

public enum PinyinExceptionEnum {
   PARSE_ERROR("C2201", "拼音转化异常，具体信息：{}");

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
