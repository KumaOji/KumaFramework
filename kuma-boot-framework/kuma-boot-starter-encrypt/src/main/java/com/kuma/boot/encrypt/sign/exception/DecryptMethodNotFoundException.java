package com.kuma.boot.encrypt.sign.exception;

public class DecryptMethodNotFoundException extends RuntimeException {
   public DecryptMethodNotFoundException() {
      super("Decryption method is not defined. (解密方式未定义)");
   }

   public DecryptMethodNotFoundException(String message) {
      super(message);
   }
}
