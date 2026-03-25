package com.kuma.boot.openapi.common.handler;

import com.kuma.boot.openapi.common.enums.SymmetricCryEnum;
import com.kuma.boot.openapi.common.handler.symmetric.AESSymmetricCryHandler;
import com.kuma.boot.openapi.common.handler.symmetric.SM4SymmetricCryHandler;
import java.util.EnumMap;
import java.util.Map;

public interface SymmetricCryHandler {
   Map handlerMap = new EnumMap(SymmetricCryEnum.class) {
      {
         this.put(SymmetricCryEnum.AES, new AESSymmetricCryHandler());
         this.put(SymmetricCryEnum.SM4, new SM4SymmetricCryHandler());
      }
   };

   String cry(String content, byte[] keyBytes);

   byte[] cry(byte[] content, byte[] keyBytes);

   String deCry(String content, byte[] keyBytes);

   byte[] deCry(byte[] content, byte[] keyBytes);
}
