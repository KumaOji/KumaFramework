package com.kuma.boot.encrypt.enums;

public enum EncryptType {
   BASE64("base64"),
   CUSTOM("自定义"),
   AES("对称加密,需指定秘钥"),
   RSA("非对称加密,需指定公钥和私钥");

   private final String describe;

   private EncryptType(String describe) {
      this.describe = describe;
   }

   public String getDescribe() {
      return this.describe;
   }

   // $FF: synthetic method
   private static EncryptType[] $values() {
      return new EncryptType[]{BASE64, CUSTOM, AES, RSA};
   }
}
