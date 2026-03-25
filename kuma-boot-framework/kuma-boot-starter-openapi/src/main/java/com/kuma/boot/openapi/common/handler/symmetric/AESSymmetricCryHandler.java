package com.kuma.boot.openapi.common.handler.symmetric;

import cn.hutool.crypto.symmetric.AES;
import com.kuma.boot.openapi.common.handler.SymmetricCryHandler;

public class AESSymmetricCryHandler implements SymmetricCryHandler {
   public String cry(String content, byte[] keyBytes) {
      AES aes = new AES(keyBytes);
      return aes.encryptBase64(content);
   }

   public byte[] cry(byte[] content, byte[] keyBytes) {
      AES aes = new AES(keyBytes);
      return aes.encrypt(content);
   }

   public String deCry(String content, byte[] keyBytes) {
      AES aes = new AES(keyBytes);
      return aes.decryptStr(content);
   }

   public byte[] deCry(byte[] content, byte[] keyBytes) {
      AES aes = new AES(keyBytes);
      return aes.decrypt(content);
   }
}
