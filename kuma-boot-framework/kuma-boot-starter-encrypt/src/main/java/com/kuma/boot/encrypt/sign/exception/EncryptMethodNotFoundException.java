package com.kuma.boot.encrypt.sign.exception;

public class EncryptMethodNotFoundException extends RuntimeException {
   public EncryptMethodNotFoundException() {
      super("Encryption method is not defined. (加密方式未定义)");
   }

   public EncryptMethodNotFoundException(String message) {
      super(message);
   }
}
