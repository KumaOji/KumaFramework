package com.kuma.boot.encrypt.handler.impl;

import com.kuma.boot.encrypt.handler.EncryptHandler;
import java.util.Base64;

public class Base64EncryptHandler implements EncryptHandler {
   public byte[] encode(byte[] content) {
      return Base64.getEncoder().encode(content);
   }

   public byte[] decode(byte[] content) {
      return Base64.getDecoder().decode(content);
   }
}
