package com.kuma.boot.openapi.common.enums;

public enum CryModeEnum {
   UNKNOWN,
   NONE,
   ASYMMETRIC_CRY,
   SYMMETRIC_CRY;

   // $FF: synthetic method
   private static CryModeEnum[] $values() {
      return new CryModeEnum[]{UNKNOWN, NONE, ASYMMETRIC_CRY, SYMMETRIC_CRY};
   }
}
