package com.kuma.boot.openapi.common.enums;

public enum SymmetricCryEnum {
   AES,
   SM4;

   // $FF: synthetic method
   private static SymmetricCryEnum[] $values() {
      return new SymmetricCryEnum[]{AES, SM4};
   }
}
