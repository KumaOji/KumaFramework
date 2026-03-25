package com.kuma.boot.encrypt.exception;

public class EncryptException extends RuntimeException {
   public EncryptException() {
   }

   public EncryptException(String message) {
      super(message);
   }

   public EncryptException(String message, Throwable t) {
      super(message, t);
   }
}
