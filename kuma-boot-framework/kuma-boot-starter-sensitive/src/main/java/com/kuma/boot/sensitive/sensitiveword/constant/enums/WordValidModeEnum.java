package com.kuma.boot.sensitive.sensitiveword.constant.enums;

public enum WordValidModeEnum {
   FAIL_FAST,
   FAIL_OVER;

   private WordValidModeEnum() {
   }

   // $FF: synthetic method
   private static WordValidModeEnum[] $values() {
      return new WordValidModeEnum[]{FAIL_FAST, FAIL_OVER};
   }
}
