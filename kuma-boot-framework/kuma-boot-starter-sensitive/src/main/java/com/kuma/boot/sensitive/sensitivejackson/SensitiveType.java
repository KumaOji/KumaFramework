package com.kuma.boot.sensitive.sensitivejackson;

public enum SensitiveType {
   CHINESE_NAME,
   ID_CARD,
   FIXED_PHONE,
   MOBILE_PHONE,
   ADDRESS,
   EMAIL,
   BANK_CARD,
   CNAPS_CODE;

   private SensitiveType() {
   }

   // $FF: synthetic method
   private static SensitiveType[] $values() {
      return new SensitiveType[]{CHINESE_NAME, ID_CARD, FIXED_PHONE, MOBILE_PHONE, ADDRESS, EMAIL, BANK_CARD, CNAPS_CODE};
   }
}
