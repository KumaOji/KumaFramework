package com.kuma.boot.pinyin.constant.enums;

public enum PinyinStyleEnum {
   NORMAL,
   DEFAULT,
   NUM_LAST,
   FIRST_LETTER,
   INPUT;

   private PinyinStyleEnum() {
   }

   // $FF: synthetic method
   private static PinyinStyleEnum[] $values() {
      return new PinyinStyleEnum[]{NORMAL, DEFAULT, NUM_LAST, FIRST_LETTER, INPUT};
   }
}
