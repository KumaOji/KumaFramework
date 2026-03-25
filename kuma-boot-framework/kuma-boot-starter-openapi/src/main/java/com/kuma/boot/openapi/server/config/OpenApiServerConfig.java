package com.kuma.boot.openapi.server.config;

import com.kuma.boot.openapi.common.enums.AsymmetricCryEnum;
import com.kuma.boot.openapi.common.enums.CryModeEnum;
import com.kuma.boot.openapi.common.enums.SymmetricCryEnum;

public interface OpenApiServerConfig {
   String getSelfPrivateKey();

   String getCallerPublicKey(String callerId);

   AsymmetricCryEnum getAsymmetricCry();

   boolean retEncrypt();

   default CryModeEnum getCryMode() {
      return CryModeEnum.SYMMETRIC_CRY;
   }

   default SymmetricCryEnum getSymmetricCry() {
      return SymmetricCryEnum.AES;
   }

   default boolean enableDoc() {
      return true;
   }

   default boolean enableCompress() {
      return false;
   }
}
