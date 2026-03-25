package com.kuma.boot.encrypt.sign.enums;

public enum EncryptBodyMethod {
   MD5,
   DES,
   AES,
   SHA,
   RSA;

   // $FF: synthetic method
   private static EncryptBodyMethod[] $values() {
      return new EncryptBodyMethod[]{MD5, DES, AES, SHA, RSA};
   }
}
