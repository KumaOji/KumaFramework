package com.kuma.boot.encrypt.sign.enums;

public enum DecryptBodyMethod {
   DES,
   AES,
   RSA;

   // $FF: synthetic method
   private static DecryptBodyMethod[] $values() {
      return new DecryptBodyMethod[]{DES, AES, RSA};
   }
}
