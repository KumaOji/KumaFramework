package com.kuma.boot.monitor.alarm.core.exception;

public class RegisterInfoIllegalException extends RuntimeException {
   public RegisterInfoIllegalException(String message) {
      super(message);
   }

   public synchronized Throwable fillInStackTrace() {
      return this;
   }
}
