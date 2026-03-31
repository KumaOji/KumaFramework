package com.kuma.boot.monitor.alarm.core.exception;

public class IllegalAlarmConfigException extends RuntimeException {
   public IllegalAlarmConfigException(String message) {
      super(message);
   }

   public synchronized Throwable fillInStackTrace() {
      return this;
   }
}
