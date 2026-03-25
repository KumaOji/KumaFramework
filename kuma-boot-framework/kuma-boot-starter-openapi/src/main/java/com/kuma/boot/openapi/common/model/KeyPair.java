package com.kuma.boot.openapi.common.model;

public class KeyPair {
   private String privateKey;
   private String publicKey;

   public KeyPair() {
   }

   public KeyPair(String privateKey, String publicKey) {
      this.privateKey = privateKey;
      this.publicKey = publicKey;
   }

   public String getPrivateKey() {
      return this.privateKey;
   }

   public void setPrivateKey(String privateKey) {
      this.privateKey = privateKey;
   }

   public String getPublicKey() {
      return this.publicKey;
   }

   public void setPublicKey(String publicKey) {
      this.publicKey = publicKey;
   }
}
