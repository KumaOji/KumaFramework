package com.kuma.boot.openapi.common.handler;

import com.kuma.boot.openapi.common.enums.AsymmetricCryEnum;
import com.kuma.boot.openapi.common.handler.asymmetric.RSAAsymmetricCryHandler;
import com.kuma.boot.openapi.common.handler.asymmetric.SM2AsymmetricCryHandler;
import java.util.EnumMap;
import java.util.Map;

public interface AsymmetricCryHandler {
   Map handlerMap = new EnumMap(AsymmetricCryEnum.class) {
      {
         this.put(AsymmetricCryEnum.RSA, new RSAAsymmetricCryHandler());
         this.put(AsymmetricCryEnum.SM2, new SM2AsymmetricCryHandler());
      }
   };

   String sign(String privateKey, String content);

   String sign(String privateKey, byte[] content);

   boolean verifySign(String publicKey, String content, String sign);

   boolean verifySign(String publicKey, byte[] content, String sign);

   String cry(String publicKey, String content);

   byte[] cry(String publicKey, byte[] content);

   String deCry(String privateKey, String content);

   byte[] deCry(String privateKey, byte[] content);
}
