package com.kuma.boot.encrypt.sign.exception;

public class DecryptBodyFailException extends RuntimeException {
   public DecryptBodyFailException() {
      super("Decrypting data failed. (解密数据失败)");
   }

   public DecryptBodyFailException(String message) {
      super(message);
   }
}
