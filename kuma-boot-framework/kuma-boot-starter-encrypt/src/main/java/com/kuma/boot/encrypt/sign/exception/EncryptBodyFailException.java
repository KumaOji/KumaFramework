package com.kuma.boot.encrypt.sign.exception;

public class EncryptBodyFailException extends RuntimeException {
   public EncryptBodyFailException() {
      super("Encrypted data failed. (加密数据失败)");
   }

   public EncryptBodyFailException(String message) {
      super(message);
   }
}
