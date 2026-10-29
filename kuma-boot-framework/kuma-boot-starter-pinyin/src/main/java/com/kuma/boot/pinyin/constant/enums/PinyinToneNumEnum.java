package com.kuma.boot.pinyin.constant.enums;

public enum PinyinToneNumEnum {
   ONE(1, "阴平"),
   TWO(2, "阳平"),
   THREE(3, "上声"),
   FOUR(4, "去声"),
   FIVE(5, "轻声"),
   UN_KNOWN(-1, "未知");

   private final int num;
   private final String desc;

   private PinyinToneNumEnum(int num, String desc) {
      this.num = num;
      this.desc = desc;
   }

   public int num() {
      return this.num;
   }

   public String desc() {
      return this.desc;
   }

   public String toString() {
      return "PinyinToneNumEnum{num=" + this.num + ", desc='" + this.desc + "'}";
   }

   public static boolean isPing(final int toneNum) {
      return ONE.num == toneNum || TWO.num == toneNum;
   }

   public static boolean isZe(final int toneNum) {
      return THREE.num == toneNum || FOUR.num == toneNum;
   }

   public static boolean isSoftly(final int toneNum) {
      return FIVE.num == toneNum;
   }

   // $FF: synthetic method
   private static PinyinToneNumEnum[] $values() {
      return new PinyinToneNumEnum[]{ONE, TWO, THREE, FOUR, FIVE, UN_KNOWN};
   }
}
