package com.kuma.boot.openapi.common.enums;

public enum AsymmetricCryEnum {
   RSA,
   SM2;

   // $FF: synthetic method
   private static AsymmetricCryEnum[] $values() {
      return new AsymmetricCryEnum[]{RSA, SM2};
   }
}
