package com.kuma.boot.openapi.common.util;

import cn.hutool.crypto.KeyUtil;
import com.kuma.boot.openapi.common.model.KeyPair;

public class AsymmetricCryUtil {
   public static KeyPair generateSM2Keys() {
      java.security.KeyPair pair = KeyUtil.generateKeyPair("SM2");
      String privateKey = Base64Util.bytesToBase64(pair.getPrivate().getEncoded());
      String publicKey = Base64Util.bytesToBase64(pair.getPublic().getEncoded());
      return new KeyPair(privateKey, publicKey);
   }

   public static KeyPair generateRSAKeys() {
      java.security.KeyPair pair = KeyUtil.generateKeyPair("RSA");
      String privateKey = Base64Util.bytesToBase64(pair.getPrivate().getEncoded());
      String publicKey = Base64Util.bytesToBase64(pair.getPublic().getEncoded());
      return new KeyPair(privateKey, publicKey);
   }
}
