package com.kuma.boot.encrypt.sign.enums;

import cn.hutool.crypto.asymmetric.KeyType;
import java.io.Serializable;

public enum RSAKeyType implements Serializable {
   PUBLIC(1, KeyType.PublicKey),
   PRIVATE(2, KeyType.PrivateKey);

   public final int type;
   public final KeyType toolType;

   private RSAKeyType(int type, KeyType toolType) {
      this.type = type;
      this.toolType = toolType;
   }

   public int getType() {
      return this.type;
   }

   public KeyType getToolType() {
      return this.toolType;
   }

   // $FF: synthetic method
   private static RSAKeyType[] $values() {
      return new RSAKeyType[]{PUBLIC, PRIVATE};
   }
}
