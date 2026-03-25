package com.kuma.boot.encrypt.sign.enums;

public enum SHAEncryptType {
   SHA1,
   SHA256;

   // $FF: synthetic method
   private static SHAEncryptType[] $values() {
      return new SHAEncryptType[]{SHA1, SHA256};
   }
}
