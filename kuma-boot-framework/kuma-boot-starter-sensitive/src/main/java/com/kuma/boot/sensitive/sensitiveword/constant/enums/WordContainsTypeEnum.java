package com.kuma.boot.sensitive.sensitiveword.constant.enums;

public enum WordContainsTypeEnum {
   CONTAINS_PREFIX,
   CONTAINS_END,
   NOT_FOUND;

   private WordContainsTypeEnum() {
   }

   // $FF: synthetic method
   private static WordContainsTypeEnum[] $values() {
      return new WordContainsTypeEnum[]{CONTAINS_PREFIX, CONTAINS_END, NOT_FOUND};
   }
}
