package com.kuma.boot.encrypt.sign.exception;

public class IllegalSecurityTypeException extends RuntimeException {
   public IllegalSecurityTypeException() {
      super("illegal security type. (非法的安全类型)");
   }

   public IllegalSecurityTypeException(String message) {
      super(message);
   }
}
