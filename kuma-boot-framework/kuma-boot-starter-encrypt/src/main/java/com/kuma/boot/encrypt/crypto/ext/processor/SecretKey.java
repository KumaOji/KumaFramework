package com.kuma.boot.encrypt.crypto.ext.processor;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.io.Serializable;
import java.sql.Timestamp;

public class SecretKey implements Serializable {
   private String identity;
   private String symmetricKey;
   private String publicKey;
   private String privateKey;
   private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

   public String getIdentity() {
      return this.identity;
   }

   public void setIdentity(String identity) {
      this.identity = identity;
   }

   public String getSymmetricKey() {
      return this.symmetricKey;
   }

   public void setSymmetricKey(String symmetricKey) {
      this.symmetricKey = symmetricKey;
   }

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

   public Timestamp getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(Timestamp timestamp) {
      this.timestamp = timestamp;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SecretKey secretKey = (SecretKey)o;
         return Objects.equal(this.identity, secretKey.identity) && Objects.equal(this.timestamp, secretKey.timestamp);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hashCode(new Object[]{this.identity, this.timestamp});
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("identity", this.identity).add("symmetricKey", this.symmetricKey).add("publicKey", this.publicKey).add("privateKey", this.privateKey).add("timestamp", this.timestamp).toString();
   }
}
