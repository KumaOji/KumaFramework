package com.kuma.boot.openapi.common.handler.symmetric;

import cn.hutool.crypto.symmetric.SM4;
import com.kuma.boot.openapi.common.handler.SymmetricCryHandler;

public class SM4SymmetricCryHandler implements SymmetricCryHandler {
   public String cry(String content, byte[] keyBytes) {
      SM4 sm4 = new SM4(keyBytes);
      return sm4.encryptBase64(content);
   }

   public byte[] cry(byte[] content, byte[] keyBytes) {
      SM4 sm4 = new SM4(keyBytes);
      return sm4.encrypt(content);
   }

   public String deCry(String content, byte[] keyBytes) {
      SM4 sm4 = new SM4(keyBytes);
      return sm4.decryptStr(content);
   }

   public byte[] deCry(byte[] content, byte[] keyBytes) {
      SM4 sm4 = new SM4(keyBytes);
      return sm4.decrypt(content);
   }
}
