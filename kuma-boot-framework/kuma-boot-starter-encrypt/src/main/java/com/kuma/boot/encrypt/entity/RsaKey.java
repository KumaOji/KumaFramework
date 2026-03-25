package com.kuma.boot.encrypt.entity;

public class RsaKey {
   private String publicKey;
   private String privateKey;

   public String getPublicKey() {
      return this.publicKey;
   }

   public void setPublicKey(String publicKey) {
      this.publicKey = publicKey;
   }

   public String getPrivateKey() {
      return this.privateKey;
   }

   public void setPrivateKey(String privateKey) {
      this.privateKey = privateKey;
   }
}
